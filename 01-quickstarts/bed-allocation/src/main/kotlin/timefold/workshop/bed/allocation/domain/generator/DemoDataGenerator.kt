package timefold.workshop.bed.allocation.domain.generator

import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.stereotype.Component
import timefold.workshop.bed.allocation.domain.BedPlan
import timefold.workshop.bed.allocation.domain.Department
import timefold.workshop.bed.allocation.domain.Room

@Component
class DemoDataGenerator {

    companion object: KLoggingChannel() {
        val SPECIALTIES: MutableList<String> = mutableListOf("Specialty1", "Specialty2", "Specialty3")
        const val TELEMETRY: String = "telemetry"
        const val TELEVISION: String = "television"
        const val OXYGEN: String = "oxygen"
        const val NITROGEN: String = "nitrogen"
        val EQUIPMENTS: MutableList<String> = mutableListOf(TELEMETRY, TELEVISION, OXYGEN, NITROGEN)
    }

    fun generateDemoData(): BedPlan {
        val schedule = BedPlan()

        // Department
        val dept1 = Department(id = "1", name = "Department")
        dept1.specialtyToPriority.put(SPECIALTIES[0], 1)
        dept1.specialtyToPriority.put(SPECIALTIES[1], 2)
        dept1.specialtyToPriority.put(SPECIALTIES[2], 3)
        schedule.departments.add(dept1)

        // Rooms
        val countRooms = 10
        val rooms = generateRooms(countRooms, schedule.departments)
        schedule.departments[0].rooms.addAll(rooms)
        schedule.rooms.addAll(rooms)



        return schedule
    }

    fun generateRooms(count: Int, departments: MutableList<Department>): MutableList<Room> {
        val rooms = mutableListOf<Room>()
        for (i in 1..count) {
            val room = Room(id = i.toString(), name = "Room $i", department = departments.first())
            rooms.add(room)
            departments.first().rooms.add(room)
        }
        return rooms
    }
}
