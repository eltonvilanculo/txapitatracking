package mmconsultoria.co.mz.mbelamova.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class StatsPagerAdapter extends FragmentPagerAdapter {


    public StatsPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TripListFragment();
            case 1:
                return new SummaryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Viagens";
            case 1:
                return "Resumo";
            default:
                return "";
        }
    }
}
