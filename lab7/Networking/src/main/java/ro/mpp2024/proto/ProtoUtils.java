package ro.mpp2024.proto;

import Org.Example.ProtocolProto;
import com.google.protobuf.InvalidProtocolBufferException;
import ro.mpp2024.Agentie;
//import com.google.protobuf.util.JsonFormat;
//import org.example.Agentie;
//import org.example.Response;
//import org.example.Response.ResponseType;

public class ProtoUtils {

    public static ProtocolProto.Response createOkResponse() {
        // Create a response for a successful request
        ProtocolProto.Response response = ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
        return response;
    }

    public static ProtocolProto.Response createErrorResponse(String err) {
        ProtocolProto.Response response = ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(err).build();
        return response;
    }

    public static ProtocolProto.Agentie fromAgentie(Agentie agentie) {
        ProtocolProto.Agentie.Builder agentieBuilder = ProtocolProto.Agentie.newBuilder()
                .setId(agentie.getId())
                .setUsername(agentie.getUsername())
                .setPass(""); // Assuming 'Pass' field is of string type
        return agentieBuilder.build();
    }

    public static Agentie getAgentie(ProtocolProto.Agentie agentie) {
        Agentie agentieDTO = new Agentie( agentie.getUsername());
        return agentieDTO;
    }

    public static Agentie getAgentieFromRequest(ProtocolProto.Request request) {
        Agentie agency = new Agentie( request.getAgentie().getUsername());
        return agency;
    }
}
