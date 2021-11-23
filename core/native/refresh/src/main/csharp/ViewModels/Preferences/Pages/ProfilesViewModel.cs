﻿using ch.cyberduck.core;
using ch.cyberduck.core.profiles;
using Ch.Cyberduck.Core.Refresh.Models;
using Ch.Cyberduck.Core.Refresh.Services;
using DynamicData;
using DynamicData.Binding;
using org.apache.log4j;
using ReactiveUI;
using ReactiveUI.Fody.Helpers;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reactive;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using System.Threading.Tasks;

namespace Ch.Cyberduck.Core.Refresh.ViewModels.Preferences.Pages
{
    using java.util;

    public class ProfilesViewModel : ReactiveObject
    {
        private static Logger log = Logger.getLogger(typeof(ProfilesViewModel).FullName);

        private readonly Dictionary<ProfileDescription, DescribedProfile> installed = new();

        public ProfilesViewModel(ProtocolFactory protocols, ProfileListObserver profileListObserver, Controller controller)
        {
            BehaviorSubject<bool> initialized = new(false);
            LoadProfiles = ReactiveCommand.CreateFromTask(async () =>
            {
                TaskCompletionSource<IEnumerable<ProfileDescription>> result = new();
                var worker = new InternalProfilesSynchronizeWorker(protocols, result);
                var action = new ProfilesWorkerBackgroundAction(controller, worker);
                controller.background(action);

                try
                {
                    Busy = true;
                    return await result.Task;
                }
                finally
                {
                    Busy = false;
                }
            }, initialized.Select(x => !x));

            var profiles = LoadProfiles.ToObservableChangeSet()
                .Filter(x => x.getProfile().isPresent())
                .Filter(this.WhenAnyValue(v => v.FilterText)
                    .Throttle(TimeSpan.FromMilliseconds(500))
                    .DistinctUntilChanged()
                    .Select(v => (Func<ProfileDescription, bool>)new SearchProfilePredicate(v).test))
                .Transform(x => new ProfileViewModel(x))
                .AsObservableList();

            profiles.Connect()
                .Sort(SortExpressionComparer<ProfileViewModel>.Ascending(x => x.Profile))
                .ObserveOnDispatcher()
                .Bind(Profiles)
                .Subscribe();

            profiles.Connect()
                .WhenPropertyChanged(v => v.Installed, false)
                .Subscribe(p =>
                {
                    if (p.Value)
                    {
                        protocols.register(p.Sender.Profile);
                    }
                    else
                    {
                        protocols.unregister(p.Sender.Profile);
                    }
                    profileListObserver.RaiseProfilesChanged();
                });
        }

        [Reactive]
        public bool Busy { get; set; }

        [Reactive]
        public string FilterText { get; set; }

        public ReactiveCommand<Unit, IEnumerable<ProfileDescription>> LoadProfiles { get; }

        public BindingList<ProfileViewModel> Profiles { get; } = new();

        private class InternalProfilesSynchronizeWorker : ProfilesSynchronizeWorker
        {
            private readonly TaskCompletionSource<IEnumerable<ProfileDescription>> completionSource;

            public InternalProfilesSynchronizeWorker(ProtocolFactory protocolFactory, TaskCompletionSource<IEnumerable<ProfileDescription>> completionSource) : base(protocolFactory, ProfilesFinder.Visitor.Prefetch)
            {
                this.completionSource = completionSource;
            }

            public override void cleanup(object result) => completionSource.SetResult(Utils.ConvertFromJavaList<ProfileDescription>((Collection)result));
        }
    }
}
