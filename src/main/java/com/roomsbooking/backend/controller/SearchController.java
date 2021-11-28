package com.roomsbooking.backend.controller;

import com.roomsbooking.backend.service.SearchService;
import com.roomsbooking.controller.SearchApi;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.SearchPayload;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "search")
@AllArgsConstructor
public class SearchController implements SearchApi {

    private final SearchService searchService;

    @Override
    public ResponseEntity<List<DetailedRoomPayload>> searchRooms(SearchPayload searchPayload,
        Integer pageNumber, Integer roomsPerPage, Integer photosPerPage) {
        return ResponseEntity.ok(
            searchService.searchRooms(searchPayload, pageNumber, roomsPerPage, photosPerPage));
    }

    @Override
    public ResponseEntity<String> getNumberOfFoundRooms(SearchPayload searchPayload) {
        return ResponseEntity.ok(searchService.getNumberOfFoundRooms(searchPayload));
    }
}
