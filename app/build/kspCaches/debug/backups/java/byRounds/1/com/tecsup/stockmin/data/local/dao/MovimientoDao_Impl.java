package com.tecsup.stockmin.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tecsup.stockmin.data.local.entity.MovimientoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MovimientoDao_Impl implements MovimientoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MovimientoEntity> __insertionAdapterOfMovimientoEntity;

  public MovimientoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMovimientoEntity = new EntityInsertionAdapter<MovimientoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `movimientos` (`id`,`itemId`,`tipo`,`cantidad`,`trabajador`,`fechaMillis`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MovimientoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getItemId());
        statement.bindString(3, entity.getTipo());
        statement.bindLong(4, entity.getCantidad());
        statement.bindString(5, entity.getTrabajador());
        statement.bindLong(6, entity.getFechaMillis());
      }
    };
  }

  @Override
  public Object insertar(final MovimientoEntity movimiento,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMovimientoEntity.insertAndReturnId(movimiento);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MovimientoEntity>> observarPorItem(final long itemId) {
    final String _sql = "SELECT * FROM movimientos WHERE itemId = ? ORDER BY fechaMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, itemId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"movimientos"}, new Callable<List<MovimientoEntity>>() {
      @Override
      @NonNull
      public List<MovimientoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "itemId");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfCantidad = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad");
          final int _cursorIndexOfTrabajador = CursorUtil.getColumnIndexOrThrow(_cursor, "trabajador");
          final int _cursorIndexOfFechaMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaMillis");
          final List<MovimientoEntity> _result = new ArrayList<MovimientoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MovimientoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpItemId;
            _tmpItemId = _cursor.getLong(_cursorIndexOfItemId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final int _tmpCantidad;
            _tmpCantidad = _cursor.getInt(_cursorIndexOfCantidad);
            final String _tmpTrabajador;
            _tmpTrabajador = _cursor.getString(_cursorIndexOfTrabajador);
            final long _tmpFechaMillis;
            _tmpFechaMillis = _cursor.getLong(_cursorIndexOfFechaMillis);
            _item = new MovimientoEntity(_tmpId,_tmpItemId,_tmpTipo,_tmpCantidad,_tmpTrabajador,_tmpFechaMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MovimientoEntity>> observarTodos() {
    final String _sql = "SELECT * FROM movimientos ORDER BY fechaMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"movimientos"}, new Callable<List<MovimientoEntity>>() {
      @Override
      @NonNull
      public List<MovimientoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "itemId");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfCantidad = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad");
          final int _cursorIndexOfTrabajador = CursorUtil.getColumnIndexOrThrow(_cursor, "trabajador");
          final int _cursorIndexOfFechaMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaMillis");
          final List<MovimientoEntity> _result = new ArrayList<MovimientoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MovimientoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpItemId;
            _tmpItemId = _cursor.getLong(_cursorIndexOfItemId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final int _tmpCantidad;
            _tmpCantidad = _cursor.getInt(_cursorIndexOfCantidad);
            final String _tmpTrabajador;
            _tmpTrabajador = _cursor.getString(_cursorIndexOfTrabajador);
            final long _tmpFechaMillis;
            _tmpFechaMillis = _cursor.getLong(_cursorIndexOfFechaMillis);
            _item = new MovimientoEntity(_tmpId,_tmpItemId,_tmpTipo,_tmpCantidad,_tmpTrabajador,_tmpFechaMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
