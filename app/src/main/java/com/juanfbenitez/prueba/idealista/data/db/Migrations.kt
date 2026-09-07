package com.juanfbenitez.prueba.idealista.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Room migrations for [IdealistaDatabase]. */
object Migrations {

    /**
     * v1 -> v2: drops the redundant `isFavorite` column from `favorites`.
     *
     * A row's mere existence in this table already means the property is a favorite -
     * [FavoriteDao] only ever inserts a row (favorite) or deletes it (unfavorite), so the
     * column was always `true` and never read.
     *
     * We don't use `ALTER TABLE ... DROP COLUMN` directly because:
     * - It was only added in SQLite 3.35.0 (2021-03-12):
     *   https://sqlite.org/releaselog/3_35_0.html
     *   https://www.sqlite.org/lang_altertable.html#altertabdropcol
     * - Android bundles whatever SQLite version shipped with the device's OS build, not a
     *   version tied to this app's minSdk/Room/AGP version. Devices at this app's
     *   `minSdk = 24` (Android 7.0) ship SQLite versions far older than 3.35.0, so calling
     *   DROP COLUMN there would crash with a syntax error:
     *   https://developer.android.com/reference/android/database/sqlite/package-summary
     * - Recreating the table (create new -> copy data -> drop old -> rename) only relies on
     *   `CREATE TABLE`/`RENAME TO`, which have been supported since early SQLite, so it works
     *   on every Android version this app supports. This is also Room's own documented
     *   approach for migrations that `ALTER TABLE` can't express directly (dropping/renaming
     *   columns, changing constraints, etc.):
     *   https://developer.android.com/training/data-storage/room/migrating-db-versions
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS favorites_new (" +
                    "propertyCode TEXT NOT NULL PRIMARY KEY, " +
                    "dateFavorited INTEGER NOT NULL)"
            )
            db.execSQL(
                "INSERT INTO favorites_new (propertyCode, dateFavorited) " +
                    "SELECT propertyCode, dateFavorited FROM favorites"
            )
            db.execSQL("DROP TABLE favorites")
            db.execSQL("ALTER TABLE favorites_new RENAME TO favorites")
        }
    }
}
