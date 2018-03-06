package linkshortener;

import javax.ws.rs.*;
import javax.ws.rs.core.*;


import java.util.HashMap;
import java.util.Random;
import java.io.*;



@Path("/services")
public class Services {
    Response.ResponseBuilder rb;
    Response response;

    @Context
    UriInfo uriInfo;

//    @GET
//    @Path("{linkId}")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String sayPlainTextHello(@PathParam("linkId") String linkId) {
//        return "Hello " + linkId;
//    }

    @GET
    @Path("{linkId}")
    public Response toLongURL(@PathParam("linkId") String linkId) {
        String longURL = readFromFile(linkId);

        if (longURL == null) {
            rb = Response.status(404);
            response = rb.build();
        } else {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(longURL);
            rb = Response.status(302);
            response = rb.contentLocation(builder.build())
                    .build();
        }

        return response;
    }

    @POST
    @Path("{longURL}/{linkId}")
    public Response toShortURL(@PathParam("longURL") String longURL, @PathParam("linkId") String linkId) {

        String cached = readFromFile(linkId);

        if (cached != null) {
            rb = Response.status(409);
            response = rb.language("LinkID conflicts! Please change to another ID.")
                    .build();

        } else {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(generateShortURL(longURL));
            writeToFile(linkId, longURL);
            rb = Response.ok();
            response = rb.contentLocation(builder.build())
                    .language("ShortURL created successfully with ID " + linkId)
                    .build();
        }
        return response;
    }

    private String generateShortURL(String longURL) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            Random rand = new Random();
            int num = rand.nextInt(26);
            sb.append((char)('a' + num));
        }
        return sb.toString();
    }

    private void writeToFile(String Id, String longURL) {
        try {
            FileWriter writer = new FileWriter("MyFile.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(Id + " " + longURL);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(String Id) {
        String res = null;
        try {
            FileReader reader = new FileReader("MyFile.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] keyValue = new String[2];
                keyValue = line.split(" ");
                if (keyValue[0].equals(Id)) {
                    res = keyValue[1];
                    break;
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return res;
        }
    }
}
