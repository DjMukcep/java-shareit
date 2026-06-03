package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody NewItemRequest newItemRequest) {
        return requestService.addRequest(newItemRequest, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) {
        return requestService.getRequest(userId,requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return  requestService.getRequests(userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getUserRequests(userId);
    }
}
