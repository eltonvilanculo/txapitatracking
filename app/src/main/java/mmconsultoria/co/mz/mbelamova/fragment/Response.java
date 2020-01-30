package mmconsultoria.co.mz.mbelamova.fragment;

import mmconsultoria.co.mz.mbelamova.cloud.CloudRepository;
import mmconsultoria.co.mz.mbelamova.cloud.RequestResult;

public class Response<T> {
    private T data;
    private RequestResult requestResult;
    private CloudRepository.DatabaseMovement movement;
    private String nextToken;
    private String key;

    public Response(T data, RequestResult requestResult, CloudRepository.DatabaseMovement movement) {
        this.data = data;
        this.requestResult = requestResult;
        this.movement = movement;
    }

    public Response() {
    }

    public T getData() {
        return data;
    }

    public Response<T> setData(T data) {
        this.data = data;
        return this;
    }

    public CloudRepository.DatabaseMovement getMovement() {
        return movement;
    }

    public Response<T> setMovement(CloudRepository.DatabaseMovement movement) {
        this.movement = movement;
        return this;
    }

    public RequestResult getRequestResult() {
        return requestResult;
    }

    public Response<T> setRequestResult(RequestResult requestResult) {
        this.requestResult = requestResult;
        return this;
    }

    public boolean hasError() {
        return requestResult != RequestResult.SUCCESSFULL;
    }

    public String getNextToken() {
        return nextToken;
    }

    public Response<T> setNextToken(String nextToken) {
        this.nextToken = nextToken;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Response<T> setKey(String key) {
        this.key = key;
        return this;
    }


    @Override
    public String toString() {
        return "Response{" +
                "data=" + data +
                ", requestResult=" + requestResult +
                ", movement=" + movement +
                ", nextToken='" + nextToken + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
