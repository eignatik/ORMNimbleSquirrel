package com.example.myapp.Util.Options;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Абстрактный класс. Все опции должны его переопределять
 * Заложено автоматическое сохранение в файл и обновление при изменении параметров создаваемых опций
 */
public abstract class AbsOptions
{


    private Map<String,OptionsItem> options=new HashMap<String,OptionsItem>();




    private SharedPreferences preferences;
    private String IS_CREATED="IS_CREATED";

    /**
     *
     * @param ctx Контекст
     * @param nameFilePref имя для файла настроке(должно быть уникальное для каждого класса настроек) типа MAIN_PREFERENCIES. Это выделит отдельный файл для хранения этого вида настроек
     */
    protected AbsOptions(Context ctx,String nameFilePref){

        preferences = ctx.getSharedPreferences(nameFilePref, Context.MODE_PRIVATE);
        if(!preferences.contains(IS_CREATED))//если такой записи нет то создадим новый файл
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_CREATED,true);
            editor.commit();

        }



        initOptions();





    }



    /**
     * Создаст опцию в массиве, но не в файле. Используется для создания опций. Автоматически перенесутся в файл
     * @param item
     * @return
     * @throws Exception
     */
    protected OptionsItem createOption(OptionsItem item) throws Exception
    {

        if(options.containsKey(item.getName())) throw new Exception("Уже есть опция с таким именем");
        options.put(item.getName(),item);


       return item;
    }

    /**
     * Можно будет вынести в определение других классов настроек
     * Нужно определить создание опций
     */
   abstract void createOptions();
   /* {
        OptionsItem item;
        try {
             item = createOption(new FloatItem((float) 0.35, "lowPorogPulse"));
             item.setMax((float) 0.35);
             item.setMin((float) 0.25);

            item=createOption(new StringItem("BiomedisPulse","fileDirApp"));

        } catch (Exception e){
            Log.v(e.getMessage());

        }


    }*/





    /**
     * Получит опции из файла настроек
     */
    private void initOptions()
    {
        createOptions();//сначала мы создадим набор опций.
        SharedPreferences.Editor editor ;

        for (Map.Entry<String, OptionsItem> entry : options.entrySet())
        {

            if(!preferences.contains(entry.getKey()+"_value"))
            {
                //создадим новый ключ, если в файле его нет.
                saveItem(entry.getValue());
            }else
            {

               if(!equalsType(entry.getKey()))   saveItem(entry.getValue());//если у нас тип не совпадает, то мы заменим на новый. иначе оставим как есть
                else
               {
                   // нужно инициализировать
                   initItemFromPref(entry.getValue());
               }
            }

        }
//удалим старые ключи, проверим есть ли соответствия в масииве
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
        {
            if(options.containsKey(entry.getKey().split("_")[0]))
            {
                editor=preferences.edit();
                editor.remove(entry.getKey());
                editor.commit();
            }

        }




    //синхронизируем с файлом(удалим лишнее если есть из файла и запушем новое),


        //то что есть в файле мы проинициализируем из файла,  имеющийся элемент нужно проверить на тип, если он не того типа что у нас в массиве по соотв имени, то нужно удалить
        // в файле и заного создать нужного типа(сделать save)






    }

    /**
     * Возвратит все ключи опций
     * @return
     */
  public Set<String>   getNamesList()
  {
     return options.keySet();
  }

private void initItemFromPref(OptionsItem item)
{
    if(item instanceof BooleanItem)
    {
        preferences.getBoolean(item.getName() + "_value", (Boolean) item.getValue());

    }else  if(item instanceof FloatItem)
    {
        item.setValue( preferences.getFloat(item.getName() + "_value", (Float) item.getValue()));
        item.setMax(preferences.getFloat(item.getName() + "_max", (Float) item.getMax()));
        item.setMin(preferences.getFloat(item.getName() + "_min", (Float) item.getMin()))  ;

    }else  if(item instanceof LongItem)
    {
        item.setValue(preferences.getLong(item.getName() + "_value", (Long) item.getValue()));
        item.setMax(preferences.getLong(item.getName() + "_max", (Long) item.getMax()));
        item.setMin(preferences.getLong(item.getName() + "_min", (Long) item.getMin()));

    }else  if(item instanceof IntItem)
    {
        item.setValue(preferences.getInt(item.getName() + "_value", (Integer) item.getValue()));
        item.setMax(preferences.getInt(item.getName() + "_max", (Integer) item.getMax()));
        item.setMin(preferences.getInt(item.getName() + "_min", (Integer) item.getMin()));

    }else  if(item instanceof StringItem)
    {
        item.setValue(preferences.getString(item.getName() + "_value", (String) item.getValue()));
        item.setMax(preferences.getString(item.getName() + "_max", (String) item.getMax()));
        item.setMin(preferences.getString(item.getName() + "_min", (String) item.getMin()));

    }else  if(item instanceof StringSetItem)
    {
        item.setValue(preferences.getStringSet(item.getName() + "_value", (Set<String>) item.getValue()));
        item.setMax(preferences.getStringSet(item.getName() + "_max", (Set<String>) item.getMax()));
        item.setMin(preferences.getStringSet(item.getName() + "_min", (Set<String>) item.getMin()));

    }




}


    /**
     * Проверит соответствует ли тип опции в массиве типу в файле
     * @param name
     * @return
     */
    private boolean equalsType(String name)
    {
        try {
            if (options.get(name+"_value") instanceof BooleanItem) preferences.getBoolean(name, false);
            if (options.get(name+"_value") instanceof LongItem) preferences.getLong(name, 0);
            if (options.get(name+"_value") instanceof IntItem) preferences.getInt(name, 0);
            if (options.get(name+"_value") instanceof FloatItem) preferences.getFloat(name, 0);
            if (options.get(name+"_value") instanceof StringItem) preferences.getString(name, "");
            if (options.get(name+"_value") instanceof StringSetItem) preferences.getStringSet(name, null);



        }catch (ClassCastException ex)
        {
            return false;
        }
        return true;
    }


    /**
     * Сохранит опцию в файл
     * @param item
     */
    private  void saveItem(OptionsItem item)
    {

        SharedPreferences.Editor editor = preferences.edit();
    //опции берутся из массивов, и не могут появиться из неоткуда, значит они точно есть!
        //здесь опция уже изменена в массиве, нужно ее в преверенс сохранить просто

        if(item instanceof BooleanItem)
        {
            editor.putBoolean(item.getName()+"_value",(Boolean)item.getValue());

        }else  if(item instanceof FloatItem)
        {
            editor.putFloat(item.getName() + "_value", (Float) item.getValue());
            editor.putFloat(item.getName()+"_max",(Float)item.getMax());
            editor.putFloat(item.getName()+"_min",(Float)item.getMin());

        }else  if(item instanceof LongItem)
        {
            editor.putLong(item.getName() + "_value", (Long) item.getValue());
            editor.putLong(item.getName() + "_max", (Long) item.getMax());
            editor.putLong(item.getName() + "_min", (Long) item.getMin());

        }else  if(item instanceof IntItem)
        {
            editor.putInt(item.getName() + "_value", (Integer) item.getValue());
            editor.putInt(item.getName() + "_max", (Integer) item.getMax());
            editor.putInt(item.getName() + "_min", (Integer) item.getMin());

        }else  if(item instanceof StringItem)
        {
            editor.putString(item.getName() + "_value", (String) item.getValue());
            editor.putString(item.getName() + "_max", (String) item.getMax());
            editor.putString(item.getName() + "_min", (String) item.getMin());

        }else  if(item instanceof StringSetItem)
        {
            editor.putStringSet(item.getName() + "_value", (Set<String>) item.getValue());
            editor.putStringSet(item.getName() + "_max", (Set<String>) item.getMax());
            editor.putStringSet(item.getName() + "_min", (Set<String>) item.getMin());

        }

        editor.commit();

    }

    public   void updateItemValue(OptionsItem item)
    {

        SharedPreferences.Editor editor = preferences.edit();
        //опции берутся из массивов, и не могут появиться из неоткуда, значит они точно есть!
        //здесь опция уже изменена в массиве, нужно ее в преверенс сохранить просто

        if(item instanceof BooleanItem)
        {
            editor.putBoolean(item.getName()+"_value",(Boolean)item.getValue());

        }else  if(item instanceof FloatItem)
        {
            editor.putFloat(item.getName() + "_value", (Float) item.getValue());


        }else  if(item instanceof LongItem)
        {
            editor.putLong(item.getName() + "_value", (Long) item.getValue());


        }else  if(item instanceof IntItem)
        {
            editor.putInt(item.getName() + "_value", (Integer) item.getValue());


        }else  if(item instanceof StringItem)
        {
            editor.putString(item.getName() + "_value", (String) item.getValue());


        }else  if(item instanceof StringSetItem)
        {
            editor.putStringSet(item.getName() + "_value", (Set<String>) item.getValue());


        }

        editor.commit();

    }

    public   void updateItemValue(String name)
    {
        if(options.containsKey(name))updateItemValue(options.get(name));
        else throw new RuntimeException("Нет такого поля!!!");

    }




/*

    public BooleanItem getBoolean(String name)
    {
        return optionsBoolean.get(name);
    }
    public LongItem getLong(String name)
    {
        return optionsLong.get(name);
    }
    public IntItem getInt(String name)
    {
        return optionsInt.get(name);
    }
    public FloatItem getFloat(String name)
    {
        return optionsFloat.get(name);
    }
    public StringItem getString(String name)
    {
        return optionsString.get(name);
    }
    public StringSetItem getStringSet(String name)
    {
        return optionsStringSet.get(name);
    }



*/

    public BooleanItem getBoolean(String name)
    {
        OptionsItem itm= options.get(name);
        if(itm==null) return null;
        if(itm instanceof BooleanItem) return (BooleanItem)options.get(name);
        else return null;



    }
    public LongItem getLong(String name)
    {
        OptionsItem itm= options.get(name);
        if(itm==null) return null;
        if(itm instanceof LongItem) return (LongItem)options.get(name);
        else return null;
    }
    public IntItem getInt(String name)
    {
        OptionsItem itm= options.get(name);
        if(itm==null) return null;
        if(itm instanceof IntItem) return (IntItem)options.get(name);
        else return null;
    }
    public FloatItem getFloat(String name)
    {
        OptionsItem itm= options.get(name);
        if(itm==null) return null;
        if(itm instanceof FloatItem) return (FloatItem)options.get(name);
        else return null;
    }
    public StringItem getString(String name)
    {
        OptionsItem itm= options.get(name);
        if(itm==null) return null;
        if(itm instanceof StringItem) return (StringItem)options.get(name);
        else return null;
    }
    public StringSetItem getStringSet(String name)
    {
        OptionsItem itm= options.get(name);
        if(itm==null) return null;
        if(itm instanceof StringSetItem) return (StringSetItem)options.get(name);
        else return null;
    }

public OptionsItem getOption(String name){return options.get(name);}







    /************************   Класс опции****************************************/


   public  class BooleanItem extends OptionsItem<Boolean>
    {
        public BooleanItem( Boolean value,String name)
        {
            super(true, false, value,name);
        }
    }

    public  class FloatItem extends OptionsItem<Float>
    {

        public FloatItem(Float value,String name)
        {
            super(Float.MAX_VALUE, Float.MIN_VALUE, value,name);
        }
    }

    public class LongItem extends OptionsItem<Long>
    {

        public LongItem(Long value,String name)
        {
            super(Long.MAX_VALUE, Long.MIN_VALUE, value, name);
        }
    }
    public  class IntItem extends OptionsItem<Integer>
    {

        public IntItem(Integer value,String name)
        {
            super(Integer.MAX_VALUE, Integer.MIN_VALUE, value, name);
        }
    }
    public class StringItem extends OptionsItem<String>
    {

        public StringItem(String value,String name)
        {
            super("", "", value, name);
        }

    }
    public class StringSetItem extends OptionsItem<Set<String>>
    {

        public StringSetItem(Set<String> value,String name)
        {
            super(null,null, value, name);
        }

    }


    public abstract    class OptionsItem<T>
    {
        T value;
        T max;
        T min;
        String name;
        T defaultValue;

        public OptionsItem(T max, T min, T value,String name)
        {
            this.max = max;
            this.min = min;
            this.value = value;
            this.name=name;
            defaultValue=value;

        }
public T getDefaultValue(){return defaultValue;}
        public void resetToDefault(){value=defaultValue;}
        public T getMax()
        {
            return max;
        }

        public void setMax(T max)
        {
            this.max = max;
        }

        public T getMin()
        {
            return min;
        }

        public void setMin(T min)
        {
            this.min = min;
        }

        public T getValue()
        {
            return value;
        }

        public void setValue(T value)
        {
            this.value = value;
        }

        public String getName()
        {
            return name;
        }


    }

}
