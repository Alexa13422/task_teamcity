package org.example.task_teamcity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;
@NoArgsConstructor
@AllArgsConstructor
public class CacheEntry {
    Map<String,String> commits;
    long timestamp;

}
