package com.example.myapp.sql;

import android.database.sqlite.SQLiteDatabase;
import com.example.myapp.sql.DAO.BlogDAO;
import com.example.myapp.sql.DAO.UserDAO;
import com.example.myapp.sql.DAO.UserInfoDAO;
import com.example.myapp.sql.entity.Blog;
import com.example.myapp.sql.entity.User;
import com.example.myapp.sql.entity.UserInfo;

import java.util.Date;
import java.util.List;

/**
 * Инициализация до использования. Поэтому еее вставили в OnCreate и onOpen в DBHelper
 */
public class ModelDataApp implements IModelDataApp
{

    private BlogDAO blogDAO = new BlogDAO(null);
    private UserDAO userDAO = new UserDAO(null);
    private UserInfoDAO userInfoDAO = new UserInfoDAO(null);


    @Override
    public void initModelDataApp()
    {

}

@Override
public void initDAO(SQLiteDatabase db)
{
    blogDAO.setDb(db);
    userDAO.setDb(db);
    userInfoDAO.setDb(db);
}
    @Override
 public void createTables()
 {
     blogDAO.createTable();
     userDAO.createTable();
     userInfoDAO.createTable();
 }

    @Override
    public void deleteTables() {
        blogDAO.deleteTable();
        userDAO.deleteTable();
        userInfoDAO.deleteTable();
    }

    public ModelDataApp() {

    }

    /**
     *
     * @param login
     * @param password
     * @param userName
     * @param descUser
     * @param dtBorn
     * @param gender
     * @return возвращает null, либо объект user.
     */
    public User createUser(String login, String password, String userName, String descUser, Date dtBorn, Boolean gender)  {
        User user = null;
        userDAO.beginTransaction();
        try
        {
            user = new User();
            user.setLogin(login);
            user.setPass(password);

            Integer id = userDAO.insert(user);
            if(id<0) throw new Exception("Не удалось создать user");

            UserInfo userInfo = new UserInfo();
            userInfo.setIdUser(id);
            userInfo.setUserName(userName);
            userInfo.setBornDate(dtBorn);
            userInfo.setDescUser(descUser);
            userInfo.setGender(gender);
            Integer idUserInfo = userInfoDAO.insert(userInfo);
            if(idUserInfo<0) throw new Exception("Не удалось создать userInfo");
            userDAO.setTransactionSuccessful();
        }catch(Exception exc)
        {
            user = null;
            exc.printStackTrace();

        } finally {
            userDAO.endTransaction();
            return user;
        }
    }

    /**
     *
     * @param idUser
     * @return возвращает результат 1 или -1 (в случае неудачной транзакции)
     */
    public int removeUser(Integer idUser){
        int res=1;
        userDAO.beginTransaction();
        try{
            userDAO.genericRemove("idUser = ?", new String[]{idUser.toString()}, false);
            userInfoDAO.remove(idUser, false);

            userDAO.setTransactionSuccessful();
        } catch(Exception exc){
            exc.printStackTrace();
            res=-1;
        } finally{
            userDAO.endTransaction();
            return res;
        }

    }

    /**
     *
     * @param login
     * @param password
     * @return вернет null, если пользователя не существует
     */
    public User loginUser(String login, String password) {
        List<User> userList = userDAO.genericSelect("login = ? AND pass = ?", new String[]{login, password}, null, null, null, "1");
        if(userList.isEmpty()) return null;
        else return userList.get(0);
    }

    /**
     *
     * @param title
     * @param content
     * @param dtBlog
     * @param user
     * @return возвращаем объект blog или null, если отсутсвует
     */
    public Blog createBlog(String title, String content, Date dtBlog, User user){
        Blog blogIt = new Blog();
        blogIt.setTitle(title);
        blogIt.setContent(content);
        blogIt.setDtBlog(dtBlog);
        blogIt.setIdUser(user.getId());

        Integer id = blogDAO.insert(blogIt);
        if(id>0) return blogIt;
        else return null;
    }

    public int removeBlog(Blog blog){
        return blogDAO.remove(blog.getId(), true);
    }

    public
}
