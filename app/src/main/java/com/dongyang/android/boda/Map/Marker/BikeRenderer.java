package com.dongyang.android.boda.Map.Marker;

import android.content.Context;


import com.dongyang.android.boda.Map.Model.Bike.BikeItem;
import com.dongyang.android.boda.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class BikeRenderer extends DefaultClusterRenderer<BikeItem> {
    public BikeRenderer(Context context, GoogleMap map, ClusterManager<BikeItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(BikeItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_seoul_bike));
        markerOptions.snippet(item.getPosition().toString());
        markerOptions.title(item.getTitle());
    }

    @Override
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<BikeItem> listener) {
        super.setOnClusterItemClickListener(listener);
    }

}


