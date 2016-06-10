package com.example.myapp.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.myapp.Util.Log;
import com.example.myapp.sql.annotations.Id;
import com.example.myapp.sql.annotations.PostCreationSQL;
import com.example.myapp.sql.annotations.Table;
import com.example.myapp.sql.annotations.TableField;
import com.example.myapp.sql.entity.IEntity;


import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;


/**
 * Наследуется классами DAO сущностей. Создается каждый на сессию базы.
 * Те его мы используем в DBHelper, создаем новые или просто задаем базу если обновляем.
 * Данный вид ORM требует Аннотации всех полей сущности, которые пишутся в базу. МОжно сохранять в БД произвольные объекты, которые могут сериализоваться.
 * Объекты типа Date автоматически преобразуются с временной штамп при записи и обратно при чтении
 * Не используются внешние ключи как функция базы. Они контроллируются пользователем, но программа не даст их править после создания, только если использовать специальный метод обновления
 *
 * Created by Anama on 20.10.2014.
 */
public abstract class GenericDAO<T extends IEntity> implements IDAO<T>
{

    public static String versionLib="1.1";

    private SQLiteDatabase db=null;
    private Class<T> persistentClass;

    private EntityBuilder entityBuilder=new EntityBuilder();



    /**
     * Отражает структуру сущности в промежуточном формате, для манипулированием самой сущностью
     */
    private class EntityBuilder
    {

        class Column
        {
            private Class<?> fieldEntityType;
            private int fieldDbType=-1;
            private String definition ="";
            private boolean primaryKey =false;
            private boolean foreignKey =false;
            private String name="";
            private Method methodSet=null;
            private Method methodGet=null;

            public boolean isForeignKey() {
                return foreignKey;
            }

            private void setForeignKey(boolean foreignKey) {
                this.foreignKey = foreignKey;
            }

            public Method getMethodSet() {
                return methodSet;
            }
            public Method getMethodGet() {
                return methodGet;
            }

            public String getName() {
                return name;
            }

            Column(String name) {
                this.name = name;
            }

            public Class<?> getFieldEntityType() {
                return fieldEntityType;
            }

            private void setFieldEntityType(Class<?> fieldEntityType)
            {
                this.fieldEntityType = fieldEntityType;
                String st=null;

                char[] chars = this.name.toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                st = new String(chars);


                try {

                    this.methodGet=persistentClass.getMethod("get" + st, new Class[]{});//фищем сеттер по имени поля


                } catch (NoSuchMethodException e) {

                    try {
                        this.methodGet = persistentClass.getMethod("is" + st, new Class[]{});//фищем сеттер по имени поля
                    }catch (NoSuchMethodException ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException("В сущности отсутствует метод  получения в соответствии с аннотированным полем is и get. "+st );
                    }

                }


                try {
                    this.methodSet=persistentClass.getMethod("set" + st, new Class[]{this.fieldEntityType});//фищем сеттер по имени поля

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    throw new RuntimeException("В сущности отсутствует метод установки  в соответствии с аннотированным полем. "+st );
                }
            }

            public boolean isPrimaryKey() {
                return primaryKey;
            }

            private void setPrimaryKey(boolean primaryKey) {
                this.primaryKey = primaryKey;
                pk=this;
            }

            public String getDefinition() {       return definition;     }
            private void setDefinition(String definition) {          this.definition = definition;     }

            public int getFieldDbType() {     return fieldDbType;    }
            private void setFieldDbType(int fieldDbType) {     this.fieldDbType = fieldDbType;    }


        }


        private String tableName;
        private String postCreationTableSQL="";

       private Map<String,Column> columns =new HashMap<String, Column>();
        private String DDLString="";
        //private Set<String> columnnames=null;
        private Column pk=null;

        public Column getPk() {
            return pk;
        }

        public String getDDLString() {
            return DDLString;
        }

        public String getPostCreationTableSQL() {
            return postCreationTableSQL;
        }

        private void setPostCreationTableSQL(String postCreationTableSQL) {
            this.postCreationTableSQL = postCreationTableSQL;
        }

        private void setTableName(String v){tableName=v;}
        public String getTableName(){return tableName;}
        public Map<String,Column> getColumns(){return columns;}
        public Column addColumn(String name)
        {
            Column cl=new Column(name);
            columns.put(name, cl);
            return cl;

        }

        /**
         * Генерирует строку DDL
         * @return
         */
    public String buildDDLString()
    {
        StringBuilder strb=new StringBuilder();

        strb.append("CREATE TABLE ");
        strb.append(entityBuilder.getTableName()+" (");
        strb.append( " id INTEGER PRIMARY KEY AUTOINCREMENT");


        Column col=null;
        String nm="";
        for (Map.Entry<String, Column> e : entityBuilder.getColumns().entrySet())
        {
            col=e.getValue();
            nm=e.getKey();

            if(nm.equals("id")) continue;

            int fieldDbType = col.getFieldDbType();
            switch(fieldDbType)
            {
                case  Cursor.FIELD_TYPE_BLOB:
                    strb.append(", "+nm+" BLOB "+col.getDefinition());
                    break;
                case  Cursor.FIELD_TYPE_FLOAT:
                    strb.append(", "+nm+" FLOAT "+col.getDefinition());
                    break;
                case  Cursor.FIELD_TYPE_INTEGER:
                    strb.append(", "+nm+" INTEGER "+col.getDefinition());
                    break;
                case  Cursor.FIELD_TYPE_STRING:
                    strb.append(", "+nm+" STRING "+col.getDefinition());
                    break;

            }





        }
        strb.append("); ");
        strb.append(entityBuilder.getPostCreationTableSQL());



        DDLString=strb.toString();

        //Логика  генерации строки

        return DDLString;
    }




        /**
         * Список имен колонок
         * @return
         */
        public String[] getColumnNames()
        {
       // if(columnnames!=null) return columnnames.toArray(new String[0]);

        return this.columns.keySet().toArray(new String[0]);



        }
        /**
         * Список имен колонок
         * @return
         */
        public Set<String> getColumnNamesList()
        {
           // if(columnnames!=null) return columnnames;
            return this.columns.keySet();



        }


    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public GenericDAO(SQLiteDatabase db) {
        this.db = db;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        //Далее мы анализируем данные класса, его поля и типы, аннотации

        try {
            analizeEntity();
        } catch (Exception e) {
            Log.v(e.getMessage()+" Ошибка при анализа класса сущности "+this.persistentClass.getCanonicalName());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage()+" Ошибка при анализа класса сущности "+this.persistentClass.getCanonicalName() );

        }

    }

    /**
     * Создает в базе данных таблицу согласно описанию в Entity
     */
    public void createTable()
    {
        if(this.isExistTable()) {
            Log.v("Таблица " + this.entityBuilder.getTableName() + " уже существует");return;}
        String ddlCreate= this.entityBuilder.buildDDLString();
        Log.v(ddlCreate);
        db.beginTransaction();
        try {
            db.execSQL(ddlCreate);
            db.setTransactionSuccessful();
            Log.v("Таблица создана "+ this.entityBuilder.getTableName());
        }catch (Exception ex) {
            Log.v("Ошибка создания таблицы  "+ entityBuilder.getTableName());
            Log.v("Код DDL  "+ ddlCreate);
            ex.printStackTrace();
            throw new RuntimeException("Ошибка создания таблицы  "+ entityBuilder.getTableName() );

        }finally {
            db.endTransaction();

        }

    }

    //TODO Нужно еще удалять индексы и тп что было созданно как доп SQL. Нужно это проработать
public void deleteTable()
{
    if(!this.isExistTable()) {Log.v("Таблица "+this.entityBuilder.getTableName()+" отсутствует");return;}
    db.beginTransaction();
    try {
        db.execSQL("DROP TABLE "+this.entityBuilder.getTableName()+";");
        db.setTransactionSuccessful();
        Log.v("Таблица удалена "+ this.entityBuilder.getTableName());

    }catch (Exception ex) {

        Log.v("Ошибка удаления таблицы "+this.entityBuilder.getTableName());
        ex.printStackTrace();
       // throw new RuntimeException("Ошибка удаления таблицы  "+ entityBuilder.getTableName() );
    }finally {
        db.endTransaction();

    }



}
    private void analizeEntity() throws Exception {
        boolean pkFounded=false;

        Log.v("Анализируется сущность "+this.persistentClass);

        if(persistentClass.isAnnotationPresent(Table.class)) entityBuilder.setTableName(persistentClass.getSimpleName());
        else throw new Exception("Использование не анатированного класса сущности  " +this.persistentClass.getCanonicalName() );

        //дополнительный SQL после создания таблицы
        if(persistentClass.isAnnotationPresent(PostCreationSQL.class))entityBuilder.setPostCreationTableSQL(persistentClass.getAnnotation(PostCreationSQL.class).SQL());




            String name="";
        TableField annotation=null;
        Id pk=null;
        for (Field field : persistentClass.getDeclaredFields())
        {

            if(!field.isAnnotationPresent(TableField.class))  if(!field.isAnnotationPresent(Id.class)) continue;




            name = field.getName();

            EntityBuilder.Column column = entityBuilder.addColumn(name);//добавим столбец с определением




            column.setFieldEntityType(field.getType());

            if(field.isAnnotationPresent(Id.class))
            {
                column.setPrimaryKey(true);
                column.setFieldDbType(Cursor.FIELD_TYPE_INTEGER);
                pkFounded=true;

            }
            else
            {
                annotation = (TableField)field.getAnnotation(TableField.class);//получим анотацию описание
                column.setDefinition(annotation.columnDefinition());
                column.setForeignKey(annotation.foreignKey());

              if(field.getType().isPrimitive())
                {

                    if(field.getType().getName().equals("int") || field.getType().getName().equals("short")  || field.getType().getName().equals("long") ||  field.getType().getName().equals("boolean")) {
                        column.setFieldDbType(Cursor.FIELD_TYPE_INTEGER);

                    }
                    else if(field.getType().getName().equals("float")   || field.getType().getName().equals("double")) {
                        column.setFieldDbType(Cursor.FIELD_TYPE_FLOAT);

                    }

                }
                else
                {
                    if(field.getType().equals(String.class))
                    {
                        column.setFieldDbType(Cursor.FIELD_TYPE_STRING);

                    }
                    else if( field.getType().equals(Integer.class)||field.getType().equals(Boolean.class)|| field.getType().equals(Short.class) || field.getType().equals(Long.class))
                    {
                        column.setFieldDbType(Cursor.FIELD_TYPE_INTEGER);

                    }
                    else if(field.getType().equals(Float.class) || field.getType().equals(Double.class)  ) {
                        column.setFieldDbType(Cursor.FIELD_TYPE_FLOAT);

                    }
                    else if(field.getType().equals(Date.class)) {
                        column.setFieldDbType(Cursor.FIELD_TYPE_INTEGER);

                    }else   column.setFieldDbType(Cursor.FIELD_TYPE_BLOB);  //тогда у нас сериализуемое поле BLOB



                }

            }






        }

       if(pkFounded==false) throw new RuntimeException("Отсутствует первичный ключ в   "+ persistentClass );

        //entityBuilder.columnnames=new HashSet<String>(entityBuilder.columns.keySet());//установка списка

    }

   /*
   int delete(String table, String whereClause, String[] whereArgs)
   void execSQL(String sql, Object[] bindArgs)
   insert(String table, String nullColumnHack, ContentValues values)
   rawQuery(String sql, String[] selectionArgs)
   query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
update(String table, ContentValues values, String whereClause, String[] whereArgs)




    */


    /**
     * Вызывает Setter в объекте entity.  Работает с аннотированными полями. Те произвольный setter не вызвать, только тот чее поле аннотированно TableField
     * @param nameField имя поля для setter
     * @param val значение к присовению
     * @param entity объект сущности в котором будет вызов
     * @return true если все нормально
     */
   private boolean callEntitySetter(String nameField, Object val,T entity)
   {
       EntityBuilder.Column column = entityBuilder.getColumns().get(nameField);
       if(column==null){Log.v("Аннотированное поле "+nameField+" Отсутствует в "+persistentClass.getCanonicalName());throw new RuntimeException("Аннотированное поле "+nameField+" Отсутствует в "+persistentClass.getCanonicalName() );}

       Object[] m=new Object[1];
       m[0]=column.fieldEntityType.cast(val);//преобразуем тип к типу поля в сущности.


       try {
           column.getMethodSet().invoke(entity,m);
       } catch (IllegalAccessException e) {
           e.printStackTrace();
           throw new RuntimeException("Не возможно вызвать setter  "+ persistentClass.getCanonicalName() );
       } catch (InvocationTargetException e) {
           e.printStackTrace();
           throw new RuntimeException("Не возможно вызвать setter  "+ persistentClass.getCanonicalName() );

       }

return true;
   }


    /**
     * Вызывает getter на entity для поля с именем nameField, аннотированным TableField
     * @param nameField
     * @param entity
     * @return Объект который вернул Getter
     */
    private Object callEntityGetter(String nameField,T entity)
    {
        EntityBuilder.Column column = entityBuilder.getColumns().get(nameField);
        if(column==null){Log.v("Аннотированное поле "+nameField+" Отсутствует в "+persistentClass.getCanonicalName());  throw new RuntimeException("Аннотированное поле "+nameField+" Отсутствует в "+persistentClass.getCanonicalName() );}

Object res=null;


        try {
            res=column.getMethodGet().invoke(entity,new Object[]{});



        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Не возможно вызвать getter  "+ persistentClass.getCanonicalName() );
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Не возможно вызвать getter  "+ persistentClass.getCanonicalName() );
        }

        return res;
    }



    public Class<T> getPersistentClass(){return persistentClass;}

    /**
     * Возвращает объект поля из базы по индексу из курсора. Курсор должен стоять в нужной позиции
     * @param c Курсор
     * @param colIndex Индекс столбца
     * @param nameField Имя столбца
     * @return
     */
    private Object getObjectFormCursor(Cursor c,int colIndex,String nameField)
    {
        Object o=null;
        byte[] s=null;

        int fieldDbType = entityBuilder.getColumns().get(nameField).getFieldDbType();
        switch(fieldDbType)
        {
            case  Cursor.FIELD_TYPE_BLOB:
                s= c.getBlob(colIndex);
            break;
            case  Cursor.FIELD_TYPE_FLOAT:
                o= c.getFloat(colIndex);

                break;
            case  Cursor.FIELD_TYPE_INTEGER:
                o= c.getInt(colIndex);

                break;
            case  Cursor.FIELD_TYPE_STRING:
                o= c.getString(colIndex);
                break;
            default:
                throw new RuntimeException("Отсутствует типа поля БД для "+nameField + " "+ persistentClass.getCanonicalName() );

        }

        if(entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Long.class) || entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(long.class))
        {
            o=c.getLong(colIndex);
        }else
        if(entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Double.class) || entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(double.class))
        {
            o=c.getDouble(colIndex);
        }else
        if(entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Date.class))
        {
            Date dt=new Date();
            o=c.getLong(colIndex);
            dt.setTime((Long)o);

            o=dt;
        }else
        if(entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Boolean.class))
        {
            Boolean b=true;
            if(c.getInt(colIndex)<1)  b=false;
            o=b;
        }

        if(s!=null)
        {
            try {
                o=unserialize(s);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("Не поулучилось десериализовать объект " + nameField + "  из сущности " + this.persistentClass.getCanonicalName());
                throw new RuntimeException("Не поулучилось десериализовать объект "+ nameField  +"  из сущности "+ this.persistentClass.getCanonicalName() );
            }
        }

        return o;
    }

    /**
     * Добавляет параметр в ContentValues, считывая его из entity по имени поля nameField, учитывае fieldDbType (типо поля БД)
     * @param nameField Название поля в сущности
     * @param fieldDbType тип поля БД
     * @param cv ContentValues для заполнения
     * @param entity сущность с данными
     */
    private void putContentValue(String nameField,int fieldDbType, ContentValues cv,T entity)
    {

        switch (fieldDbType) {

            case Cursor.FIELD_TYPE_BLOB:
                cv.put(nameField, serialize(this.callEntityGetter(nameField, entity)));
                break;

            case Cursor.FIELD_TYPE_FLOAT:

                if (entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Float.class) || entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(float.class)  )cv.put(nameField, (Float) this.callEntityGetter(nameField, entity));
                else  if (entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Double.class) || entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(double.class)  )cv.put(nameField, (Double) this.callEntityGetter(nameField, entity));



                break;

            case Cursor.FIELD_TYPE_INTEGER:
                if (entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Long.class) || entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(long.class)  ) cv.put(nameField, (Long) this.callEntityGetter(nameField, entity));
               else
                if (entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Date.class)) {
                    Date dt = (Date) this.callEntityGetter(nameField, entity);
                   if(dt==null)cv.put(nameField, (long)0); else cv.put(nameField, (long) dt.getTime());

                } else  if(entityBuilder.getColumns().get(nameField).getFieldEntityType().equals(Boolean.class))
                {
                    int i=0;
                    Boolean b=(Boolean)this.callEntityGetter(nameField,entity);
                    if(b==true) i=1;

                    cv.put(nameField,i);

                }
                else cv.put(nameField, (Integer) this.callEntityGetter(nameField, entity));

                break;

            case Cursor.FIELD_TYPE_STRING:
                cv.put(nameField, (String) this.callEntityGetter(nameField, entity));
                break;

        }
    }




    @Override
    public T findById(Integer id) {

        List<T> ts = this.genericSelect("id=?", new String[]{id.toString()}, null, null, null, "1");
        return ts.get(0);
    }

    @Override
    public List<T> findAll() {

        return this.genericSelect(null,null,null,null,this.entityBuilder.getPk().getName(),null);


    }

    @Override
    public List<T> findAll(int first, int max) {

        return this.genericSelect(null,null,null,null,this.entityBuilder.getPk().getName(),first+","+max);

    }

    /**
     * Обновдяет указанные поля.
     * @param entity  сущность
     * @param columnsToUpdate список колонок для апдейта
     * @param safely безопасно или нет(с возможостью перезаписи полей внешних ключей или безопасно без возможности)
     * @return
     */
    @Override
  synchronized public boolean update(T entity,String[] columnsToUpdate,boolean safely)
   {

       if(columnsToUpdate.length==0) return false;

       ContentValues cv=new ContentValues();
       boolean res=false;
       int fieldDbType=-1;
       if(entity.getId() > 0) {
           for (String n : columnsToUpdate) {
               if(!this.entityBuilder.getColumnNamesList().contains(n)) throw new RuntimeException("Указанное поле для обновления отсутствует в сущности");//если указанное поле  не присутствует в сущности то мы остановим программу.
               if (entityBuilder.getColumns().get(n).isPrimaryKey()) continue;//первичный ключ не используем, он у нас авто

               if (safely) if (entityBuilder.getColumns().get(n).isForeignKey()) continue;//игнорим внешние ключи

               fieldDbType = entityBuilder.getColumns().get(n).getFieldDbType();


               putContentValue(n, fieldDbType,  cv, entity);


           }


           if(db.update(this.entityBuilder.getTableName(), cv, "id=?", new String[]{entity.getId().toString()})>0) res=true;
       }

           return res;
   }




//TODO Разобраться - если подаются в сущности поля с null
    /**
     * Создаст запись. Поле ID передаваемого объекта автоматически изменется на новое
     * @param entity
     * @return ID записи при взтавке.
     */
    @Override
   synchronized public Integer insert(T entity)
    {
        ContentValues cv=new ContentValues();
        Integer newId=0;
        int fieldDbType=-1;

if(this.entityBuilder.getColumnNames().length==1) Log.v("НЕОБХОДИМО ЧТОБЫ БЫЛО ПОЛЕ КРОМЕ ID хотябы одно");

            for(String n: this.entityBuilder.getColumnNames())
            {



                if(entityBuilder.getColumns().get(n).isPrimaryKey())
                {


                    continue;//первичный ключ не используем, он у нас авто
                }

                fieldDbType= entityBuilder.getColumns().get(n).getFieldDbType();

                putContentValue(n, fieldDbType,  cv, entity);

            }


            newId =   (int)db.insert(this.entityBuilder.getTableName(), null, cv);
         entity.setId(newId);

        return newId;
    }

    /**
     * Обновление записи
     * Здесь делается MERGE. Те полное копирование в поля базы. Те заполнять сущность нужно полностью иначе сотрум поля.
     *
     * @param entity
     * @param safely true - не перепыисывать внешние ключи. false переписывать внешние ключи. При id =0 это не важно.
     * @return ID записи при взтавке.
     */
    @Override
   synchronized public Integer save(T entity,boolean safely) throws Exception {
        ContentValues cv=new ContentValues();
        Integer newId=0;
        int fieldDbType=-1;
    if(entity.getId() > 0)
    {
        for(String n: this.entityBuilder.getColumnNames())
        {
            if(entityBuilder.getColumns().get(n).isPrimaryKey())continue;//первичный ключ не используем, он у нас авто
            if(safely)if(entityBuilder.getColumns().get(n).isForeignKey())continue;//игнорим внешние ключи

            fieldDbType= entityBuilder.getColumns().get(n).getFieldDbType();
            putContentValue(n, fieldDbType,  cv, entity);

        }


      return  db.update(this.entityBuilder.getTableName(),cv,"id=?",new String[]{entity.getId().toString()});

    }else throw new Exception("Попытка обновить запись базы без указания ID");


    }

    @Override
    public Integer forceSave(T entity) throws Exception {
        return save(entity,false);
    }

    @Override
    public Integer safelySave(T entity) throws Exception {
        return save(entity,true);
    }




    @Override
    public List<T> genericSelect(String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) {

        List<T> entities=new ArrayList<T>();
        T entity=null;

        Cursor c=db.query(this.entityBuilder.getTableName(),this.entityBuilder.getColumnNames(),whereClause,whereArgs,groupBy,having,orderBy,limit);

        if(c!=null)
        {
            String[] columnNames = this.entityBuilder.getColumnNames();
            if (c.moveToFirst()) {

                do {

                    try {
                        entity = this.persistentClass.newInstance();

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if(entity==null) {Log.v("не удалось создать объект сущности "+this.persistentClass.getCanonicalName());return null;}
                    entities.add(entity);


                    for (String cn : columnNames) {
                        this.callEntitySetter(cn,this.getObjectFormCursor(c,c.getColumnIndex(cn),cn),entity);//вызываем setters для каждой сущности

                    }

                } while (c.moveToNext());
            }
        }

        return entities;


    }

    /**
     *
     * @param fields набор полей которые будут  извлекаться или не извлекаться в зависимости от exclusive параметра
     * @param exclusive если true то fields воспринимается как список который нужно исключить из извлечения, если false тот что нужно включить в спиоок(будет извлечено только то что в списке)
     * @param whereClause
     * @param whereArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    @Override
    public List<T> genericSelect(String[] fields, boolean exclusive,String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) {

        List<T> entities=new ArrayList<T>();
        T entity=null;

        Map<String,String> fieldsmap=new HashMap<String, String>();
        for(String s:fields)fieldsmap.put(s,s);

        Cursor c=db.query(this.entityBuilder.getTableName(),this.entityBuilder.getColumnNames(),whereClause,whereArgs,groupBy,having,orderBy,limit);

        if(c!=null)
        {
            String[] columnNames = this.entityBuilder.getColumnNames();
            if (c.moveToFirst()) {

                do {

                    try {
                        entity = this.persistentClass.newInstance();

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if(entity==null) {Log.v("не удалось создать объект сущности "+this.persistentClass.getCanonicalName());return null;}
                    entities.add(entity);



                    for (String cn : columnNames)
                    {
                        if(exclusive==true)
                        {
                            if(fieldsmap.containsKey(cn)) continue;
                        }else
                        {
                            if(!fieldsmap.containsKey(cn)) continue;
                        }
                        this.callEntitySetter(cn,this.getObjectFormCursor(c,c.getColumnIndex(cn),cn),entity);//вызываем setters для каждой сущности

                    }

                } while (c.moveToNext());
            }
        }

        return entities;


    }
    /**
     * Удаление
     * @param whereClause типа "id>? and id<?"
     * @param whereArgs списока аргументов на замену ?
     * @param inTransaction если true то будут транзакция на этот вызов, если false, то мы сами должны управлять транзакцией. Например чтобы объеденить несколько вызовов, те если данный вызов вызывается внутри другой транзакуии
     * @return
     */
    @Override
    public int genericRemove(String whereClause, String[] whereArgs,boolean inTransaction) {
        int res=0;

       if(inTransaction) {


           db.beginTransaction();
           try {
               res = db.delete(this.entityBuilder.getTableName(), whereClause, whereArgs);
               db.setTransactionSuccessful();
           } catch (Exception ex) {
               Log.v("Ошибка удаления .");
               ex.printStackTrace();
           } finally {
               db.endTransaction();
               return res;
           }
       }else return db.delete(this.entityBuilder.getTableName(), whereClause, whereArgs);

    }

    @Override
    public int remove(Integer id,boolean inTransaction)
    {

        return genericRemove("id=?",new String[]{id.toString()},inTransaction);


    }



    @Override
    public int remove(T entity,boolean inTransaction)
    {

        return  remove(entity.getId(),inTransaction);

    }
    @Override
    public void nonSelectRawSql(String sql, Object[] bindArgs) {

        db.execSQL(sql+";",bindArgs);
    }

    /**
     * Вернет колличество записей в таблице. или -1 если что-то не так.
     * @return
     */
    @Override
    public int countAll()
    {
        int res=-1;
        Cursor cursor = this.rawSQL("SELECT COUNT(*) FROM " + entityBuilder.getTableName() + ";", null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
           res= cursor.getInt(0);
        }
    return  res;
    }

    @Override
    public int genericCount(String whereClause, String[] whereArgs) {

        int res=-1;
        Cursor cursor = this.rawSQL("SELECT COUNT(*) FROM " + entityBuilder.getTableName() +" WHERE "+whereClause+ " ;", whereArgs);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            res= cursor.getInt(0);
        }
        return  res;

    }

    @Override
    public void updateSafely() {

    }

    @Override
    public Cursor rawSQL(String sql, String[] bindArgs) {
        return db.rawQuery(sql,bindArgs);
    }


    private  byte serialize(Object obj)[]
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(obj);

            return bos.toByteArray();
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.v("Ошибка сериализации объекта "+obj.toString());
            return null;
        }
    }




    private  Object unserialize(byte[] serialized) throws IOException
    {
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Object) ois.readObject();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            Log.v("Ошибка десериализации объекта "+serialized.toString());
            return null;
        }
    }


    public void beginTransaction() { db.beginTransaction(); }
    public void setTransactionSuccessful(){db.setTransactionSuccessful();}
    public void endTransaction(){db.endTransaction(); }

    public boolean isExistTable()
    {
        boolean ret=false;
        Cursor cursor = this.rawSQL("SELECT count(name) FROM sqlite_master where name=?", new String[]{this.entityBuilder.getTableName()});
        if(cursor!=null)
        {
            cursor.moveToFirst();
            if(cursor.getInt(0)>0)ret= true;

        }
        return ret;
    }
}
