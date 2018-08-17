package com.jaram.jarambuild.roomDb;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class UserListViewModel extends AndroidViewModel
{

    private final LiveData<List<User>> userList;

    private AppDatabase appDatabase;

    public UserListViewModel(Application application)
    {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        userList = appDatabase.getUserDao().getAllUsers();

        //requestedUser = getOneUser(email);
    }

    public LiveData<List<User>> getUserList()
    {
        return userList;
    }

    public void addOneUser(User user)
    {
        new insertAsyncTask(appDatabase).execute(user);
    }

    public void deleteOneUser(User user)
    {
        new deleteAsyncTask(appDatabase).execute(user);
    }

    private static class deleteAsyncTask extends AsyncTask<User, Void, Void>
    {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final User... params)
        {
            db.getUserDao().deleteUser(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void>
    {

        private AppDatabase db;

        insertAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final User... params)
        {
            db.getUserDao().insertUser(params[0]);
            return null;
        }
    }
}
