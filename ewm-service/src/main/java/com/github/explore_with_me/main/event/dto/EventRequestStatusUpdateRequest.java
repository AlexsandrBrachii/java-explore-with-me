package com.github.explore_with_me.main.event.dto;

import java.util.List;

import com.github.explore_with_me.main.requests.status.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;
    private Status status;
}