package util.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.NumberUtils;
import util.data.Return;
import util.logger.log;

/**
 * http://www.bulksms.com/int/docs/eapi/status_reports/http_push/
 *
 */
public class BulkSms {

    private String userName;
    private String userPass;

    public BulkSms(String userName, String userPass) {
        this.userName = userName;
        this.userPass = userPass;
    }

    public Return<BulkSmsStatus> sendSMS(String mobileNumber, String message) throws Exception {
        return sendSMS(mobileNumber, message, false);
    }

    public Return<BulkSmsStatus> sendSMS(String mobileNumber, String message, boolean unicode) throws Exception {
        Return<BulkSmsStatus> ret = new Return<BulkSmsStatus>();
        BulkSmsStatus bulkSmsStatus = BulkSmsStatus.NO_SENDING;
        
        if (unicode)
            message = stringToHex(message);

        // Prepare messsage stream
        StringBuilder data = new StringBuilder();
        data.append("username=" + URLEncoder.encode(userName, "ISO-8859-1"));
        data.append("&password=" + URLEncoder.encode(userPass, "ISO-8859-1"));
        data.append("&message=" + URLEncoder.encode(message, "ISO-8859-1"));

        if (unicode)
            data.append("&dca=16bit");

        data.append("&want_report=1");
        data.append("&msisdn=" + mobileNumber);

        OutputStreamWriter wr = null;
        BufferedReader rd = null;
        try {
            URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
            /*
             * If your firewall blocks access to port 5567, you can fall back to port 80:
             * URL url = new URL("http://bulksms.vsms.net/eapi/submission/send_sms/2/2.0");
             */
            log.info("  bulksms start sending to: %s, size: %d", mobileNumber, message.length());

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data.toString());
            wr.flush();

            // Get the response
            Pattern pNum = Pattern.compile("\\d+"); 
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Matcher m = pNum.matcher(line);
                Integer code = 0;
                if (m.find()) {
                    String gi = m.group();
                    code =  NumberUtils.integerOf(gi, null);
                    bulkSmsStatus = BulkSmsStatus.getStatus(code);
                }

                log.trace("  bulksms: return core: %d (%s)", code, line);
            }
            log.info("  bulksms finish sending to: %s, size: %d", mobileNumber, message.length());
        } catch (Exception e) {
            throw e;
        } finally {
            if (wr != null)
                wr.close();
            if (rd != null)
                rd.close();
        }
        
        ret.setReturnObject(bulkSmsStatus);
        return ret;
    }

    static public String stringToHex(String s) {
        char[] chars = s.toCharArray();
        String next;
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            next = Integer.toHexString((int) chars[i]);
            // Unfortunately, toHexString doesn't pad with zeroes, so we have to.
            for (int j = 0; j < (4 - next.length()); j++) {
                output.append("0");
            }
            output.append(next);
        }
        return output.toString();
    }

    // static public void main(String[] args) throws Exception {
    // BulkSms bs = new BulkSms("davor_sauer", "dw0rk1n");
    // bs.sendSMS("385918989089", "testna poruka 2");
    //
    // }
    
    
    //
    // ovo radi, moje ne radi.. isti kood ??
    // PROVJERITI
    static public void main(String[] args) {
        try {
            // Construct data
            String data = "";
            /*
             * Note the suggested encoding for certain parameters, notably
             * the username, password and especially the message. ISO-8859-1
             * is essentially the character set that we use for message bodies,
             * with a few exceptions for e.g. Greek characters. For a full list,
             * see: http://bulksms.vsms.net/docs/eapi/submission/character_encoding/
             */
            data += "username=" + URLEncoder.encode("davor_sauer", "ISO-8859-1");
            data += "&password=" + URLEncoder.encode("dw0rk1n", "ISO-8859-1");
            data += "&message=" + URLEncoder.encode("This is a test.", "ISO-8859-1");
            data += "&want_report=1";
            data += "&msisdn=385918989089";

            // Send data
            URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
            /*
             * If your firewall blocks access to port 5567, you can fall back to port 80:
             * URL url = new URL("http://bulksms.vsms.net/eapi/submission/send_sms/2/2.0");
             * (See FAQ for more details.)
             */

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Print the response output...
                System.out.println(line);
            }
            wr.close();
            rd.close();
            System.out.println("..Finish..");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
