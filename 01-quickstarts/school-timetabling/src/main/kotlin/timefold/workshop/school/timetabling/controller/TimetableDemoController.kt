package timefold.workshop.school.timetabling.controller

import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import timefold.workshop.school.timetabling.domain.Timetable
import timefold.workshop.school.timetabling.domain.TimetableProvider
import timefold.workshop.school.timetabling.domain.TimetableProvider.DataSizeType

@RestController
@RequestMapping("/demo-data")
class TimetableDemoController {

    companion object: KLoggingChannel()

    private val timetableCache = HashMap<DataSizeType, Timetable>()

    @GetMapping
    suspend fun getList(): List<DataSizeType> = DataSizeType.entries

    @GetMapping("/{dataSizeType}")
    suspend fun generate(
        @PathVariable("dataSizeType") dataSizeType: DataSizeType,
    ): Timetable {
        return timetableCache[dataSizeType]
            ?: TimetableProvider.generateTimetable(dataSizeType)
                .apply {
                    timetableCache[dataSizeType] = this
                }
    }
}
