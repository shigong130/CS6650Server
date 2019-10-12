import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {
    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SkierServlet.class);

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        logger.info("Post : " + urlPath);

        // check we have a URL!
        if (urlPath == null || urlPath.length() == 0) {
            setNotFoundResponse(res);
            return;
        }

        String[] urlParts = urlPath.split("/");


        if (!isUrlValid(urlParts)) {
            setNotFoundResponse(res);
        } else if (urlParts[2].equals("seasons")) {
            handlePostSeasons(req, res, urlParts);
        } else{
            setNotFoundResponse(res);
        }

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        logger.info(urlPath);

        // check we have a URL!
        if (urlPath == null || urlPath.length() == 0) {
            setNotFoundResponse(res);
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            setNotFoundResponse(res);
        } else if(urlParts[2].equals("vertical")) {
            handleGetVertical(req, res, urlParts);
        } else if (urlParts[2].equals("seasons")) {
            handleGetSeasons(res, urlParts);
        } else{
            setNotFoundResponse(res);
        }
    }

    private void handleGetVertical(HttpServletRequest req, HttpServletResponse res,  String[] url) throws IOException{

        String resortStr = req.getParameter("resort");
        String seasonStr = req.getParameter("season");
        if(resortStr==null || resortStr.length()==0) {
            setInvalidParaResponse(res);
            return;
        }

        int skierId;
        try{
            skierId = Integer.valueOf(url[1]);
        }catch(Exception e){
            setInvalidParaResponse(res);
            return;
        }

        if(skierId<0){
            setNotFoundResponse(res);
            return;
        }

        res.setStatus(HttpServletResponse.SC_OK);

        String dummy = "{\n" +
                "  \"resorts\": [\n" +
                "    {\n" +
                "      \"seasonID\": \"string\",\n" +
                "      \"totalVert\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        res.getWriter().write(dummy);
    }

    private void handleGetSeasons(HttpServletResponse res, String[] url) throws IOException{
        int resortId, seasonId, dayId, skierId;
        try{
            resortId = Integer.valueOf(url[1]);
            seasonId = Integer.valueOf(url[3]);
            dayId = Integer.valueOf(url[5]);
            skierId = Integer.valueOf(url[7]);
        }catch(Exception e){
            setInvalidParaResponse(res);
            return;
        }

        if(resortId<0 || seasonId<0 || dayId<0 || skierId<0){
            setNotFoundResponse(res);
            return;
        }

        res.setStatus(HttpServletResponse.SC_OK);
        String dummy = "34507";
        res.getWriter().write(dummy);
    }

    private void handlePostSeasons(HttpServletRequest req, HttpServletResponse res, String[] url) throws IOException{
        long time, liftId;
        long resortId, seasonId, dayId, skierId;

        String str, wholeStr = "";
        try {
            resortId = Long.valueOf(url[1]);
            seasonId = Long.valueOf(url[3]);
            dayId = Long.valueOf(url[5]);
            skierId = Long.valueOf(url[7]);

            BufferedReader br = req.getReader();
            while ((str = br.readLine()) != null) {
                wholeStr += str;
            }

            Object obj = new JSONParser().parse(wholeStr);
            JSONObject jo = (JSONObject) obj;

            Object timeObj = jo.get("time");
            Object liftObj = jo.get("liftID");
            time = (Long) timeObj;
            liftId = (Long) liftObj;

            if(resortId<0 || seasonId<0 || dayId<0 || skierId<0 || time<0 || liftId<0){
                setNotFoundResponse(res);
                return;
            }

            res.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            setInvalidParaResponse(res);
            return;
        }
    }

    private void setInvalidParaResponse(HttpServletResponse res) throws IOException{
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String message = "{\n" +
                "  \"message\": \"InvalidParameters\"\n" +
                "}";
        res.getWriter().write(message);
    }

    private void setNotFoundResponse(HttpServletResponse res) throws IOException{
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String message = "{\n" +
                "  \"message\": \"Data not found\"\n" +
                "}";
        res.getWriter().write(message);
    }

    private boolean isUrlValid(String[] url) {
        if(url==null || (url.length!=3 && url.length!=8) ) return false;
        return true;
    }
}
