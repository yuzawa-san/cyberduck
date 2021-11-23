package ch.cyberduck.core.s3;

import ch.cyberduck.core.BytecountStreamListener;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.SimplePathPredicate;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.io.BandwidthThrottle;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.DisabledStreamListener;
import ch.cyberduck.core.kms.KMSEncryptionFeature;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class S3MultipartUploadServiceTest extends AbstractS3Test {

    @Test
    public void testUploadSinglePart() throws Exception {
        final S3MultipartUploadService service = new S3MultipartUploadService(session, new S3WriteFeature(session), 5 * 1024L * 1024L, 2);
        final Path container = new Path("test-eu-central-1-cyberduck", EnumSet.of(Path.Type.directory, Path.Type.volume));
        final String name = String.format(" %s.txt", UUID.randomUUID().toString());
        final Path test = new Path(container, name, EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), name);
        final String random = new RandomStringGenerator.Builder().build().generate(1000);
        IOUtils.write(random, local.getOutputStream(false), Charset.defaultCharset());
        final TransferStatus status = new TransferStatus();
        status.setLength(random.getBytes().length);
        status.setMime("text/plain");
        status.setStorageClass(S3Object.STORAGE_CLASS_REDUCED_REDUNDANCY);
        final BytecountStreamListener count = new BytecountStreamListener();
        service.upload(test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED),
                count, status, new DisabledLoginCallback());
        assertEquals(random.getBytes().length, count.getSent());
        assertTrue(status.isComplete());
        assertTrue(new S3FindFeature(session).find(test));
        final PathAttributes attributes = new S3AttributesFinderFeature(session).find(test);
        assertEquals(random.getBytes().length, attributes.getSize());
        assertEquals(Checksum.NONE, attributes.getChecksum());
        assertNotNull(attributes.getETag());
        // d2b77e21aa68ebdcbfb589124b9f9192-1
        assertEquals(Checksum.NONE, Checksum.parse(attributes.getETag()));
        assertEquals(S3Object.STORAGE_CLASS_REDUCED_REDUNDANCY, new S3StorageClassFeature(session).getClass(test));
        final Map<String, String> metadata = new S3MetadataFeature(session, new S3AccessControlListFeature(session)).getMetadata(test);
        assertFalse(metadata.isEmpty());
        assertEquals("text/plain", metadata.get("Content-Type"));
        new S3DefaultDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }

    @Test
    public void testUploadSinglePartEncrypted() throws Exception {
        final S3MultipartUploadService service = new S3MultipartUploadService(session, new S3WriteFeature(session), 5 * 1024L * 1024L, 2);
        final Path container = new Path("test-eu-central-1-cyberduck", EnumSet.of(Path.Type.directory, Path.Type.volume));
        final String name = UUID.randomUUID().toString() + ".txt";
        final Path test = new Path(container, name, EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), name);
        final String random = new RandomStringGenerator.Builder().build().generate(1000);
        IOUtils.write(random, local.getOutputStream(false), Charset.defaultCharset());
        final TransferStatus status = new TransferStatus();
        status.setEncryption(KMSEncryptionFeature.SSE_KMS_DEFAULT);
        status.setLength(random.getBytes().length);
        status.setMime("text/plain");
        status.setStorageClass(S3Object.STORAGE_CLASS_REDUCED_REDUNDANCY);
        final BytecountStreamListener count = new BytecountStreamListener();
        service.upload(test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED),
            count, status, new DisabledLoginCallback());
        assertEquals(random.getBytes().length, count.getSent());
        assertTrue(status.isComplete());
        assertTrue(new S3FindFeature(session).find(test));
        final PathAttributes attributes = new S3AttributesFinderFeature(session).find(test);
        assertEquals(random.getBytes().length, attributes.getSize());
        assertEquals(S3Object.STORAGE_CLASS_REDUCED_REDUNDANCY, new S3StorageClassFeature(session).getClass(test));
        final Map<String, String> metadata = new S3MetadataFeature(session, new S3AccessControlListFeature(session)).getMetadata(test);
        assertFalse(metadata.isEmpty());
        assertEquals("text/plain", metadata.get("Content-Type"));
        assertEquals("aws:kms", metadata.get("server-side-encryption"));
        assertNotNull(metadata.get("server-side-encryption-aws-kms-key-id"));
        new S3DefaultDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }

    @Test(expected = NotfoundException.class)
    public void testUploadInvalidContainer() throws Exception {
        final S3MultipartUploadService m = new S3MultipartUploadService(session, new S3WriteFeature(session), 5 * 1024L * 1024L, 1);
        final Path container = new Path("nosuchcontainer.cyberduck.ch", EnumSet.of(Path.Type.directory, Path.Type.volume));
        final Path test = new Path(container, UUID.randomUUID().toString(), EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        final TransferStatus status = new TransferStatus();
        m.upload(test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), new DisabledStreamListener(), status, null);
    }

    @Test
    public void testMultipleParts() throws Exception {
        // 5L * 1024L * 1024L
        final S3MultipartUploadService m = new S3MultipartUploadService(session, new S3WriteFeature(session), 5 * 1024L * 1024L, 5);
        final Path container = new Path("test-eu-central-1-cyberduck", EnumSet.of(Path.Type.directory, Path.Type.volume));
        final Path test = new Path(container, UUID.randomUUID().toString(), EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        final int length = 5242881;
        final byte[] content = RandomUtils.nextBytes(length);
        IOUtils.write(content, local.getOutputStream(false));
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final BytecountStreamListener count = new BytecountStreamListener();
        m.upload(test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status, null);
        assertEquals(content.length, count.getSent());
        assertTrue(status.isComplete());
        assertTrue(new S3FindFeature(session).find(test));
        assertEquals(content.length, new S3AttributesFinderFeature(session).find(test).getSize());
        new S3DefaultDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }

    @Test
    public void testMultiplePartsWithSHA256Checksum() throws Exception {
        // 5L * 1024L * 1024L
        final S3MultipartUploadService m = new S3MultipartUploadService(session, new S3WriteFeature(session), 5 * 1024L * 1024L, 5);
        final Path container = new Path("test-eu-central-1-cyberduck", EnumSet.of(Path.Type.directory, Path.Type.volume));
        final Path test = new Path(container, UUID.randomUUID().toString(), EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        final int length = 5242881;
        final byte[] content = RandomUtils.nextBytes(length);
        IOUtils.write(content, local.getOutputStream(false));
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final BytecountStreamListener count = new BytecountStreamListener();
        m.upload(test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status, null);
        assertEquals(content.length, count.getSent());
        assertTrue(status.isComplete());
        assertTrue(new S3FindFeature(session).find(test));
        assertEquals(content.length, new S3AttributesFinderFeature(session).find(test).getSize());
        new S3DefaultDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }

    @Test
    public void testAppendSecondPart() throws Exception {
        final Path container = new Path("test-eu-central-1-cyberduck", EnumSet.of(Path.Type.directory, Path.Type.volume));
        final String name = UUID.randomUUID().toString();
        final Path test = new Path(container, name, EnumSet.of(Path.Type.file));
        final int length = 12 * 1024 * 1024;
        final byte[] content = RandomUtils.nextBytes(length);
        Local local = new Local(System.getProperty("java.io.tmpdir"), name);
        IOUtils.write(content, local.getOutputStream(false));
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final AtomicBoolean interrupt = new AtomicBoolean();
        final BytecountStreamListener count = new BytecountStreamListener();
        try {
            new S3MultipartUploadService(session, new S3WriteFeature(session), 10L * 1024L * 1024L, 1).upload(test, new Local(System.getProperty("java.io.tmpdir"), name) {
                    @Override
                    public InputStream getInputStream() throws AccessDeniedException {
                        return new CountingInputStream(super.getInputStream()) {
                            @Override
                            protected void beforeRead(int n) throws IOException {
                                if(this.getByteCount() >= 11L * 1024L * 1024L) {
                                    throw new IOException();
                                }
                            }
                        };
                    }
                },
                    new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status,
                    new DisabledLoginCallback());
        }
        catch(BackgroundException e) {
            // Expected
            interrupt.set(true);
        }
        assertTrue(interrupt.get());
        assertEquals(10L * 1024L * 1024L, count.getSent());
        assertFalse(status.isComplete());
        final Path upload = new S3ListService(session).list(container, new DisabledListProgressListener()).find(new SimplePathPredicate(test));
        assertTrue(new S3FindFeature(session).find(upload));
        assertNotNull(upload);
        assertTrue(upload.getType().contains(Path.Type.upload));
        assertTrue(new S3FindFeature(session).find(upload));
        assertEquals(10L * 1024L * 1024L, new S3AttributesFinderFeature(session).find(upload).getSize());
        final TransferStatus append = new TransferStatus().append(true).withLength(2L * 1024L * 1024L).withOffset(10L * 1024L * 1024L);
        new S3MultipartUploadService(session, new S3WriteFeature(session), 10L * 1024L * 1024L, 1).upload(test, local,
                new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, append,
                new DisabledConnectionCallback());
        assertEquals(12L * 1024L * 1024L, count.getSent());
        assertTrue(append.isComplete());
        assertTrue(new S3FindFeature(session).find(test));
        assertEquals(12L * 1024L * 1024L, new S3AttributesFinderFeature(session).find(test).getSize());
        final byte[] buffer = new byte[content.length];
        final InputStream in = new S3ReadFeature(session).read(test, new TransferStatus(), new DisabledConnectionCallback());
        IOUtils.readFully(in, buffer);
        in.close();
        assertArrayEquals(content, buffer);
        new S3DefaultDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }

    @Test
    public void testAppendNoPartCompleted() throws Exception {
        final Path container = new Path("test-eu-central-1-cyberduck", EnumSet.of(Path.Type.directory, Path.Type.volume));
        String name = UUID.randomUUID().toString();
        final Path test = new Path(container, name, EnumSet.of(Path.Type.file));
        final Local local = new Local(System.getProperty("java.io.tmpdir"), name);
        final int length = 32769;
        final byte[] content = RandomUtils.nextBytes(length);
        IOUtils.write(content, local.getOutputStream(false));
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final AtomicBoolean interrupt = new AtomicBoolean();
        final BytecountStreamListener count = new BytecountStreamListener();
        try {
            new S3MultipartUploadService(session, new S3WriteFeature(session), 10485760L, 1).upload(test, new Local(System.getProperty("java.io.tmpdir"), name) {
                    @Override
                    public InputStream getInputStream() throws AccessDeniedException {
                        return new CountingInputStream(super.getInputStream()) {
                            @Override
                            protected void beforeRead(int n) throws IOException {
                                if(this.getByteCount() >= 32768) {
                                    throw new IOException();
                                }
                            }
                        };
                    }
                }, new BandwidthThrottle(BandwidthThrottle.UNLIMITED), count, status,
                new DisabledConnectionCallback());
        }
        catch(BackgroundException e) {
            // Expected
            interrupt.set(true);
        }
        assertTrue(interrupt.get());
        assertEquals(0L, count.getSent());
        assertEquals(0L, status.getOffset());
        assertFalse(status.isComplete());

        final TransferStatus append = new TransferStatus().append(true).withLength(content.length);
        new S3MultipartUploadService(session, new S3WriteFeature(session), 10485760L, 1).upload(
            test, local, new BandwidthThrottle(BandwidthThrottle.UNLIMITED),
            count, append,
            new DisabledConnectionCallback());
        assertEquals(32769L, count.getSent());
        assertTrue(append.isComplete());
        assertTrue(new S3FindFeature(session).find(test));
        assertEquals(content.length, new S3AttributesFinderFeature(session).find(test).getSize());
        final byte[] buffer = new byte[content.length];
        final InputStream in = new S3ReadFeature(session).read(test, new TransferStatus(), new DisabledConnectionCallback());
        IOUtils.readFully(in, buffer);
        in.close();
        assertArrayEquals(content, buffer);
        new S3DefaultDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        local.delete();
    }
}
