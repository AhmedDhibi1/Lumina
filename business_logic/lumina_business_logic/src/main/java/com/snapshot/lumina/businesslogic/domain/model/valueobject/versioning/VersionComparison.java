package com.snapshot.lumina.businesslogic.domain.model.valueobject.versioning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.FileSize;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.VersionId;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VersionComparison {
    private VersionId version1;
    private VersionId version2;
    private FileSize sizeDifference;
    private List<String> changes;
    private Timestamp timestamp1;
    private Timestamp timestamp2;

    /*// Compare versions
    VersionComparison compareVersions(VersionId version1, VersionId version2);*/
}
