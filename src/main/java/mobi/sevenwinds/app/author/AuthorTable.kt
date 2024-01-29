package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.CurrentDateTime

object AuthorTable : IntIdTable("author") {
    val name = varchar("name", 128).uniqueIndex()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime()).nullable()
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var name by AuthorTable.name
    var createdAt by AuthorTable.createdAt

    fun toResponse(): AuthorRecord {
        return AuthorRecord(name)
    }
}