package module.sensor.sensor.vendor.davis.WeatherLink;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import module.sensor.models.DBSensorGroup;
import module.sensor.sensor.SensorGroupInterface;
import module.sensor.sensor.SensorWorker;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import util.DateUtils;
import util.FileUtils;
import util.StringUtils;
import util.ValidationUtils;
import util.logger.log;

public class WeatherLinkWorker extends SensorWorker {

    /**
     * Last modified date of file on FTP server
     */
    private Date lastFileModifiedDate = null;

    @Override
    public void work() throws Exception {
        log.debug("start::" + this.getClass().getName() + ":" + getGroupId());

        DBSensorGroup dbSG = DBSensorGroup.findById(getGroupId());
        SensorGroupInterface sg = dbSG.getGroupClass();
        
        if (sg instanceof WeatherLink) {
            WeatherLink mh = (WeatherLink) sg;

            File localFile = null;
            // Ako su definirane postavke za FTP, skini datoteku
            String host = mh.propFtp.properties().value();
            String user = mh.propUserName.properties().value();
            String pass = mh.propUserPass.properties().value();
            String[] path = new String[] { mh.propFtpFile1.properties().value(), mh.propFtpFile2.properties().value() };

            if (!StringUtils.isEmpty(host, user, pass) && ValidationUtils.hasNoNull((Object) path)) {
                progress.setProgress("Connect to FTP...");

                String downloadFile = null;

                FTPClient ftp = new FTPClient();
                boolean downloadComplete = false;
                List<File> dwFiles = new ArrayList<File>();
                try {

                    log.info("connect to ftp host: " + host);
                    ftp.connect(host);
                    log.info("connected..");

                    log.info("login with user " + user);
                    if (ftp.login(user, pass)) {
                        progress.setProgress("Connected to FTP!");

                        for (String p : path) {
                            if (p == null || p.trim().length() == 0)
                                continue;

                            FTPFile[] ftpFs = ftp.listFiles(p);
                            if (ftpFs.length == 1) {
                                log.info("File path:" + p);

                                // Check file date modified
                                FTPFile ftpF = ftpFs[0];
                                Calendar c = ftpF.getTimestamp();

                                boolean doDownload = false;
                                if (lastFileModifiedDate == null || lastFileModifiedDate.getTime() < c.getTimeInMillis()) {
                                    doDownload = true;
                                }

                                log.info("Last used file date modified: " + (lastFileModifiedDate != null ? DateUtils.getFormatedDateTime(lastFileModifiedDate) : "null"));
                                log.info("local file date modified: " + DateUtils.getFormatedDateTime(c.getTime()));

                                if (doDownload && ftpF.isFile()) {
                                    progress.setProgress("Downloading file..");
                                    downloadFile = FileUtils.path(FileUtils.getFSPath(), FileUtils.getNextFileNamePrefix() + ".txt");
                                    progress.setProgress("Download complete");

                                    // Download file from FTP
                                    File dwFile = new File(downloadFile);
                                    dwFile.createNewFile();

                                    FileOutputStream fio = new FileOutputStream(dwFile);

                                    if (ftp.retrieveFile(p, fio)) {
                                        log.info("complete file copy from ftp '" + p + "' to " + downloadFile);
                                        lastFileModifiedDate = c.getTime();
                                        downloadComplete = true;
                                    } else {
                                        log.warning("not complete file copy from ftp '" + p + "' to " + downloadFile);
                                    }

                                    fio.flush();
                                    fio.close();

                                    dwFiles.add(dwFile);
                                } else {
                                    log.info("No download");
                                }
                            } else {
                                log.warning("Can't find file '" + p + "' on FTP " + host);
                            }

                        }

                        ftp.disconnect();

                    } else {
                        log.warning("login failed with user:" + user);
                    }

                } finally {
                    if (ftp.isConnected()) {
                        log.info("disconnecting from FTP " + host);
                        ftp.disconnect();
                        progress.setProgress("Disconect from FTP");
                    }

                    if (downloadComplete == true && dwFiles.size()!=0 && downloadFile != null) {
                        for(File dwFile : dwFiles) {
                            progress.setProgress("Reading file and insert to database...");
                            int writedLines = writeFileToDatabase(dwFile);
                            progress.setProgress("User %d lines", writedLines);
                            
                            if (writedLines == 0) {
                                log.info("File not used for import data (%s), delete file...", dwFile.getAbsolutePath());
                                dwFile.delete();
                            }
                        }
                    } else {
                            for(File dwFile : dwFiles) {
                                log.info("delete temp file " + dwFile.getAbsolutePath());
                                dwFile.delete();
                            }
                    }
                }

            } else {
                progress.setProgress("Reading local file...");
                String[] localFilePath = new String[] { mh.propLocalFile1.properties().value(), mh.propLocalFile2.properties().value() };

                if (ValidationUtils.hasNoNull((Object) localFilePath)) {
                    for (String lf : localFilePath) {
                        if (lf == null)
                            continue;
                        
                        log.info("local file:" + lf);
                        localFile = new File(lf);
                        if (localFile.exists() && localFile.isFile()) {
                            Date localFileModified = new Date(localFile.lastModified());

                            log.info("Last used file date modified: " + (lastFileModifiedDate != null ? DateUtils.getFormatedDateTime(lastFileModifiedDate) : "null"));
                            log.info("local file date modified: " + DateUtils.getFormatedDateTime(localFileModified));

                            if (lastFileModifiedDate == null || lastFileModifiedDate.getTime() < localFileModified.getTime()) {
                                lastFileModifiedDate = localFileModified;
                                writeFileToDatabase(localFile);
                            }
                        }
                    }
                } else {
                    log.debug("no local file");
                }
            }

        } else {
            log.error(sg.getClass().getName() + " not instanceof " + WeatherLink.class.getName());
        }

        
        progress.setProgress("Finish");
        log.debug("end::" + this.getClass().getName() + ":" + getGroupId());
    }

    private int writeFileToDatabase(File file) throws Exception {
        WeatherLinkParseFile mhPF = new WeatherLinkParseFile(getGroupId());
        int isWrited = mhPF.writeFileToDatabase(file);

        log.info("Used line from file:" + isWrited);
        return isWrited;
    }

}
