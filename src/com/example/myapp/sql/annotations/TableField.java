package com.example.myapp.sql.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Можно анотировать все кроме char Char. Соответственно сложные данные типа массивов, классов, коллекций не будут использоваться в поиске по таблице, они просто храняться в полях
 * Created by Anama on 20.10.2014.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField
{
    /**
     * Определения поля как принято в SQL типа NOT NULL  UNIQUE  DEFAULT ( TRUE )
     * @return
     */
    String columnDefinition() default "";

    /**
     * Указывает что поле является внешним ключок к другой таблице. Поле должно быть Integer. тогда метод updateSafely() не затронит эти параметры, чтобы сохранить целостность данных
     * @return
     */
    boolean foreignKey() default false;

}
