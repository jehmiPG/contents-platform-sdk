using System;
using Jehmi.Unity;

// this is temporary class for test.
public class Login
{
    public delegate void ResponseDelegate(User result, Exception e);
    private ResponseDelegate callback = null;

    public Login(ResponseDelegate callback) 
    {
        this.callback = callback;
    }

    public void request()
    {
        long currentTimeMillis = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;

        User user = new User(currentTimeMillis.ToString());
        JCP.ShowToast("++ user id : " + user.userId);
        callback(user, null);
    }
}
