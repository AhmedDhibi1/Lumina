package com.snapshot.lumina.businesslogic.domain.model.valueobject.versioning;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.FileSize;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.VersionId;

import java.util.List;

@Value
@Builder
public class VersionComparison {
    VersionId version1;
    VersionId version2;
    FileSize sizeDifference;
    List<String> changes;
    Timestamp timestamp1;
    Timestamp timestamp2;

    public VersionComparison(VersionId version2, VersionId version1, FileSize sizeDifference, List<String> changes, Timestamp timestamp1, Timestamp timestamp2) {
        this.version2 = version2;
        this.version1 = version1;
        this.sizeDifference = sizeDifference;
        this.changes = changes;
        this.timestamp1 = timestamp1;
        this.timestamp2 = timestamp2;
    }

    /*// Compare versions
    VersionComparison compareVersions(VersionId version1, VersionId version2);*/
}
