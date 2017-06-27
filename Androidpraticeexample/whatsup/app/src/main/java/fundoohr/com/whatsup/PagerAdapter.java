package fundoohr.com.whatsup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int NumberOfTabs;
    public PagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.NumberOfTabs=NumberOfTabs;

    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Call c1 = new Call();
                return c1;
            case 1:
                Chats chats = new Chats();
                return chats;
            case 2:
                Contacts contacts = new Contacts();
                return contacts;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NumberOfTabs;
    }

}
