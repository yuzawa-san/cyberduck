﻿//
// Copyright (c) 2010-2016 Yves Langisch. All rights reserved.
// http://cyberduck.io/
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// Bug fixes, suggestions and comments should be sent to:
// feedback@cyberduck.io
//

using ch.cyberduck.core.pool;
using ch.cyberduck.core.synchronization;
using ch.cyberduck.core.transfer;
using System.Drawing;
using static Ch.Cyberduck.ImagesHelper;

namespace Ch.Cyberduck.Ui.Controller
{
    internal class SyncPromptModel : TransferPromptModel
    {
        public SyncPromptModel(TransferPromptController controller, SessionPool source, SessionPool destination, Transfer transfer)
            : base(controller, source, destination, transfer)
        {
        }

        public override object GetCreateImage(TransferItem item)
        {
            if (!GetStatus(item).isExists())
            {
                return (Image)Images.Plus;
            }
            return null;
        }

        public override object GetSyncGetter(TransferItem item)
        {
            Comparison compare = ((SyncTransfer)Transfer).compare(item);
            if (compare.equals(Comparison.remote))
            {
                return (Image)Images.TransferDownload.Size(16);
            }
            if (compare.equals(Comparison.local))
            {
                return (Image)Images.TransferUpload.Size(16);
            }
            return null;
        }
    }
}
