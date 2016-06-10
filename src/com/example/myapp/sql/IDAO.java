package com.example.myapp.sql;

import android.database.Cursor;
import com.example.myapp.sql.entity.IEntity;


import java.util.List;

/**
 * Created by Anama on 20.10.2014.
 */
public interface IDAO<T extends IEntity>
{

    public T findById(Integer id);
    public List<T> findAll();
    public List<T> findAll(int first, int max);
    public Integer save(T entity, boolean safely) throws Exception;
    public Integer forceSave(T entity) throws Exception;
    public Integer safelySave(T entity) throws Exception;
    public boolean update(T entity, String[] columnsToUpdate, boolean safely)  throws Exception;
    public Integer insert(T entity);

    public int remove(Integer id, boolean inTransaction);
    public int remove(T entity, boolean inTransaction);
    public int countAll();
    public int genericCount(String whereClause, String[] whereArgs);

    /**
     * Безопасный апдейт. Исклчает помеченные foreingKey поляю
     */
    public void updateSafely();


    public List<T> genericSelect(String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit);
    public List<T> genericSelect(String[] fields, boolean exclusive, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit);

        public int genericRemove(String whereClause, String[] whereArgs, boolean inTransaction);
    public void nonSelectRawSql(String sql, Object[] bindArgs);
    public Cursor rawSQL(String sql, String[] bindArgs);

}
