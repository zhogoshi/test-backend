package mobi.sevenwinds.app.budget

import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object BudgetTable : IntIdTable("budget") {
    val authorId = reference("author_id", AuthorTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val year = integer("year")
    val month = integer("month")
    val amount = integer("amount")
    val type = enumerationByName("type", 100, BudgetType::class)
}

class BudgetEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<BudgetEntity>(BudgetTable)

    var authorId by BudgetTable.authorId
    var year by BudgetTable.year
    var month by BudgetTable.month
    var amount by BudgetTable.amount
    var type by BudgetTable.type

    fun toResponse(authorName: String?, authorCreatedAt: DateTime?): BudgetRecord {
        return BudgetRecord(
            year,
            month,
            amount,
            type,
            authorId?.value,
            authorName,
            authorCreatedAt?.let { DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(it.toLocalDateTime()) })
    }

}