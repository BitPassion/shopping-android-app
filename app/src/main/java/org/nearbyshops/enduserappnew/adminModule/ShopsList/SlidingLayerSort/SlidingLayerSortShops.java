package org.nearbyshops.enduserappnew.adminModule.ShopsList.SlidingLayerSort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import org.nearbyshops.enduserappnew.Interfaces.NotifySort;
import org.nearbyshops.enduserappnew.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sumeet on 15/9/16.
 */

public class SlidingLayerSortShops extends Fragment {

    @BindView(R.id.sort_distance)
    TextView sort_by_distance;

    @BindView(R.id.sort_rating)
    TextView sort_by_rating;

//    @BindView(R.id.popularity)
//    TextView sort_by_popularity;

//    @BindView(R.id.available)
//    TextView sort_by_items_available;

//    @BindView(R.id.item_price)
//    TextView sort_by_item_price;



    @BindView(R.id.sort_ascending)
    TextView sort_ascending;

    @BindView(R.id.sort_descending)
    TextView sort_descending;

    String currentSort = SORT_BY_DISTANCE;
    String currentAscending = SORT_ASCENDING;

    int colorSelected = R.color.blueGrey800;
    int colorSelectedAscending = R.color.gplus_color_2;


    public static String SORT_BY_DISTANCE = "distance";
    public static String SORT_BY_RATING = "avg_rating";
    public static String SORT_BY_POPULARITY = "popularity";


    public static String SORT_DESCENDING = "DESC NULLS LAST";
    public static String SORT_ASCENDING = "ASC NULLS LAST";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sliding_sort_shops_by_category,container,false);
        ButterKnife.bind(this,view);

        loadDefaultSort();
        return view;
    }






    void loadDefaultSort() {
//        String[] sort_options = UtilitySortShopItem.getSort(getActivity());

        currentSort = PrefSortShops.getSort(getActivity());
        currentAscending = PrefSortShops.getAscending(getActivity());

        clearSelectionSort();
        clearSelectionAscending();

        if (currentSort.equals(SORT_BY_DISTANCE))
        {
            sort_by_distance.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            sort_by_distance.setBackgroundColor(ContextCompat.getColor(getActivity(), colorSelected));
        }
        else if (currentSort.equals(SORT_BY_RATING))
        {
            sort_by_rating.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
            sort_by_rating.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelected));
        }
//        else if(currentSort.equals(SORT_BY_POPULARITY))
//        {
//            sort_by_popularity.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
//            sort_by_popularity.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelected));
//
//        }


        if(currentAscending.equals(SORT_ASCENDING))
        {
            sort_ascending.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
            sort_ascending.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelectedAscending));
        }
        else if(currentAscending.equals(SORT_DESCENDING))
        {
            sort_descending.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
            sort_descending.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelectedAscending));
        }
    }



    @OnClick(R.id.sort_distance)
    void sortByNameClick(View view)
    {
        clearSelectionSort();
        sort_by_distance.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        sort_by_distance.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelected));

        PrefSortShops.saveSort(getActivity(),SORT_BY_DISTANCE);

        if(getActivity() instanceof NotifySort)
        {
            ((NotifySort)getActivity()).notifySortChanged();
        }
    }




    @OnClick(R.id.sort_rating)
    void sortByCreated(View view)
    {
        clearSelectionSort();
        sort_by_rating.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        sort_by_rating.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelected));

        PrefSortShops.saveSort(getActivity(),SORT_BY_RATING);

        if(getActivity() instanceof NotifySort)
        {
            ((NotifySort)getActivity()).notifySortChanged();
        }
    }




//    @OnClick(R.id.popularity)
//    void sortByShopCount(View view)
//    {
//        clearSelectionSort();
//        sort_by_popularity.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
//        sort_by_popularity.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelected));
//
//        PrefSortShops.saveSort(getActivity(),SORT_BY_POPULARITY);
//
//        if(getActivity() instanceof NotifySort)
//        {
//            ((NotifySort)getActivity()).notifySortChanged();
//        }
//    }







    @OnClick(R.id.sort_ascending)
    void ascendingClick(View view)
    {
        clearSelectionAscending();
        sort_ascending.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        sort_ascending.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelectedAscending));


        PrefSortShops.saveAscending(getActivity(),SORT_ASCENDING);

        if(getActivity() instanceof NotifySort)
        {
            ((NotifySort)getActivity()).notifySortChanged();
        }

    }


    @OnClick(R.id.sort_descending)
    void descendingClick(View view)
    {
        clearSelectionAscending();
        sort_descending.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
        sort_descending.setBackgroundColor(ContextCompat.getColor(getActivity(),colorSelectedAscending));


        PrefSortShops.saveAscending(getActivity(),SORT_DESCENDING);

        if(getActivity() instanceof NotifySort)
        {
            ((NotifySort)getActivity()).notifySortChanged();
        }
    }



    void clearSelectionSort()
    {
        sort_by_distance.setTextColor(ContextCompat.getColor(getActivity(),R.color.blueGrey800));
        sort_by_rating.setTextColor(ContextCompat.getColor(getActivity(),R.color.blueGrey800));
//        sort_by_popularity.setTextColor(ContextCompat.getColor(getActivity(),R.color.blueGrey800));

        sort_by_distance.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.light_grey));
        sort_by_rating.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.light_grey));
//        sort_by_popularity.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.light_grey));
    }



    void clearSelectionAscending()
    {
        sort_ascending.setTextColor(ContextCompat.getColor(getActivity(),R.color.blueGrey800));
        sort_descending.setTextColor(ContextCompat.getColor(getActivity(),R.color.blueGrey800));

        sort_ascending.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.light_grey));
        sort_descending.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.light_grey));
    }

}
