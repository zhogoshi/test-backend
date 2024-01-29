package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.format.DateTimeFormat


object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.authorId =
                    if (body.authorId == null || body.authorId == 0) null else EntityID(body.authorId, BudgetTable)
            }

            return@transaction entity.toResponse(
                body.authorName,
                body.authorCreatedAt?.let { DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(it) })
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val itemsQuery = (if (param.authorName == null) BudgetTable.leftJoin(
                AuthorTable,
                { authorId },
                { AuthorTable.id }) else BudgetTable.innerJoin(AuthorTable, { authorId }, { AuthorTable.id }))
                .slice(BudgetTable.columns + AuthorTable.name + AuthorTable.createdAt)
                .select { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month, SortOrder.ASC)
                .orderBy(BudgetTable.amount, SortOrder.DESC)
                .limit(param.limit, param.offset)

            param.authorName?.let { itemsQuery.andWhere { BudgetTable.authorId.isNull() or Op.build { AuthorTable.name.lowerCase() eq it.toLowerCase() } } }

            val items = itemsQuery.map {
                BudgetRecord(
                    year = it[BudgetTable.year],
                    month = it[BudgetTable.month],
                    amount = it[BudgetTable.amount],
                    type = it[BudgetTable.type],
                    authorId = it.getOrNull(BudgetTable.authorId)?.value,
                    authorName = if (it.hasValue(AuthorTable.name)) it[AuthorTable.name] else null,
                    authorCreatedAt = it.getOrNull(AuthorTable.createdAt)
                        ?.let { createdAt -> DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(createdAt) }
                )
            }

            var query = BudgetTable
                .select { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month, SortOrder.ASC)
                .orderBy(BudgetTable.amount, SortOrder.DESC)
            query = query
                .limit(query.count(), param.offset)
            val sumByType = BudgetEntity.wrapRows(query).map { it.toResponse(null, null) }
                .groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = query.count(),
                totalByType = sumByType,
                items = items
            )
        }
    }
}