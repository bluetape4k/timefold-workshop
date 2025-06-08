package timefold.workshop.bed.allocation.domain

import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import java.io.Serializable

data class Patient(
    val id: String = TimebasedUuid.Epoch.nextIdAsString(),
    val name: String = "patient-$id",
    var gender: Gender? = null,
    var age: Int? = null,
): Serializable 
