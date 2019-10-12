import com.sun.media.jfxmedia.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "ResortServlet")
public class ResortServlet extends HttpServlet {
    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ResortServlet.class);

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //res.setContentType("text/plain");
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.length() == 0) {
            setNotFoundResponse(res);
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            setNotFoundResponse(res);
        } else if (urlParts[2].equals("seasons")) {
            handlePostResorts(req, res, urlParts);
        } else{
            setNotFoundResponse(res);
        }

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String urlPath = req.getPathInfo();

        logger.info("Get Url: " + urlPath);

        // check we have a URL!
        if (urlPath == null || urlPath.length() == 0) {
            handleGetResorts(res);
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            setNotFoundResponse(res);
        } else if (urlParts[2].equals("seasons")) {
            handleGetResortsWithId(res, urlParts);
        } else{
            setNotFoundResponse(res);
        }
    }

    private void handleGetResorts(HttpServletResponse res) throws IOException{
        res.setStatus(HttpServletResponse.SC_OK);
        String message = "{\n" +
                "  \"resorts\": [\n" +
                "    {\n" +
                "      \"resortName\": \"skiworld\",\n" +
                "      \"resortID\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        res.getWriter().write(message);
    }

    private void handleGetResortsWithId(HttpServletResponse res, String[] url) throws IOException{
        int resortId;
        try{
            resortId = Integer.valueOf(url[1]);
        }catch(Exception e){
            setInvalidParaResponse(res);
            return;
        }

        String dummyMessage = "{\n" +
                "  \"seasons\": [\n" +
                "    \"string\"\n" +
                "  ]\n" +
                "}";

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(dummyMessage);
    }

    private void handlePostResorts(HttpServletRequest req, HttpServletResponse res, String[] url) throws IOException{
        long resortId, year;

        String str, wholeStr = "";
        try {
            resortId = Long.valueOf(url[1]);

            BufferedReader br = req.getReader();
            while ((str = br.readLine()) != null) {
                wholeStr += str;
            }

            Object obj = new JSONParser().parse(wholeStr);
            JSONObject jo = (JSONObject) obj;

            Object yearObj = jo.get("year");
            year = (Long) yearObj;

            res.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            setInvalidParaResponse(res);
            return;
        }
    }

    private void setInvalidParaResponse(HttpServletResponse res) throws IOException{
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String dummyMessage = "{\n" +
                "  \"message\": \"400 bad request: invalid parameters\"\n" +
                "}";
        res.getWriter().write(dummyMessage);
    }

    private void setNotFoundResponse(HttpServletResponse res) throws IOException{
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String dummyMessage = "{\n" +
                "  \"message\": \"404 not found : incorrect URL\"\n" +
                "}";
        res.getWriter().write(dummyMessage);
    }

    private boolean isUrlValid(String[] url) {
        if(url==null || url.length!=3 ) return false;
        return true;
    }
}
