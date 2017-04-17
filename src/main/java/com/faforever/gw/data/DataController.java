package com.faforever.gw.data;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import java.security.Principal;
import java.util.Map;

/**
 * JSON-API compliant data API.
 */
@RestController
@RequestMapping(path = DataController.PATH_PREFIX)
public class DataController {

    public static final String PATH_PREFIX = "/data";
    private static final String JSON_API_MEDIA_TYPE = "application/vnd.api+json";

    private final Elide elide;

    public DataController(Elide elide) {
        this.elide = elide;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = JSON_API_MEDIA_TYPE,
            value = {"/{entity}", "/{entity}/{id}/relationships/{entity2}", "/{entity}/{id}/{child}", "/{entity}/{id}"})
    @Transactional(readOnly = true)
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> get(@RequestParam final Map<String, String> allRequestParams,
                                      final HttpServletRequest request,
                                      final Principal principal) {
        ElideResponse response = elide.get(
                getJsonApiPath(request),
                new MultivaluedHashMap<>(allRequestParams),
                principal
        );
        return wrapResponse(response);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            produces = JSON_API_MEDIA_TYPE,
            value = {"/{entity}", "/{entity}/{id}/relationships/{entity2}", "/{entity}/{id}/{child}", "/{entity}/{id}"})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> post(@RequestBody final String body,
                                       final HttpServletRequest request,
                                       final Principal principal) {
        ElideResponse response = elide.post(
                getJsonApiPath(request),
                body,
                principal
        );
        return wrapResponse(response);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            produces = JSON_API_MEDIA_TYPE,
            value = {"/{entity}/{id}", "/{entity}/{id}/relationships/{entity2}"})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> patch(@RequestBody final String body,
                                        final HttpServletRequest request,
                                        final Principal principal) {
        ElideResponse response = elide.patch(JSON_API_MEDIA_TYPE,
                JSON_API_MEDIA_TYPE,
                getJsonApiPath(request),
                body,
                principal
        );
        return wrapResponse(response);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            produces = JSON_API_MEDIA_TYPE,
            value = {"/{entity}/{id}", "/{entity}/{id}/relationships/{entity2}"})
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> delete(final HttpServletRequest request,
                                         final Principal principal) {
        ElideResponse response = elide.delete(
                getJsonApiPath(request),
                null,
                principal
        );
        return wrapResponse(response);
    }

    private ResponseEntity<String> wrapResponse(ElideResponse response) {
        return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    private String getJsonApiPath(HttpServletRequest request) {
        return ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).replace(PATH_PREFIX, "");
    }
}