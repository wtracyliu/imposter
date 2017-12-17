package com.gatehill.imposter.plugin.openapi.util;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.converter.SwaggerConverter;

import java.util.List;
import java.util.Map;

/**
 * From https://github.com/swagger-api/swagger-parser/issues/540
 */
public class HackedSwaggerConverter extends SwaggerConverter {
    @Override
    public Operation convert(io.swagger.models.Operation v2Operation) {
        Operation ret = super.convert(v2Operation);
        if (ret != null) {
            io.swagger.models.parameters.BodyParameter bodyParam = getV2BodyParam(v2Operation);
            if (bodyParam != null) {
                Map<String, String> examples = bodyParam.getExamples();
                if (examples != null && !examples.isEmpty()) {
                    RequestBody requestBody = ret.getRequestBody();
                    if (requestBody == null) {
                        requestBody = new RequestBody();
                        ret.setRequestBody(requestBody);
                    }
                    Content content = requestBody.getContent();
                    if (content == null) {
                        content = new Content();
                        requestBody.setContent(content);
                    }
                    addExamplesIfMissing(content, bodyParam.getExamples());
                }
            }
        }
        return ret;
    }
    @Override
    public ApiResponse convert(io.swagger.models.Response v2Response, List<String> produces) {
        ApiResponse ret = super.convert(v2Response, produces);
        if (ret != null) {
            Map<String, Object> examples = v2Response.getExamples();
            if (examples != null && !examples.isEmpty()) {
                Content content = ret.getContent();
                if (content == null) {
                    content = new Content();
                    ret.setContent(content);
                }
                addExamplesIfMissing(content, v2Response.getExamples());
            }
        }
        return ret;
    }
    private static io.swagger.models.parameters.BodyParameter getV2BodyParam(io.swagger.models.Operation v2Operation) {
        List<io.swagger.models.parameters.Parameter> parameters = v2Operation.getParameters();
        if (parameters != null) {
            for (io.swagger.models.parameters.Parameter parameter: parameters) {
                if (parameter instanceof io.swagger.models.parameters.BodyParameter) {
                    return (io.swagger.models.parameters.BodyParameter) parameter;
                }
            }
        }
        return null;
    }
    private static void addExamplesIfMissing(Content content, Map<String, ?> examples) {
        for (Map.Entry<String, ?> example : examples.entrySet()) {
            String mediaTypeStr = example.getKey();
            MediaType mt = content.get(mediaTypeStr);
            if (mt == null) {
                mt = new MediaType();
                content.put(mediaTypeStr, mt);
            }
            if (mt.getExample() == null && mt.getExamples() == null) {
                mt.setExample(example.getValue());
            }
        }
    }
}
