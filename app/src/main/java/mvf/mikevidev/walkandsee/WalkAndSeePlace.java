package mvf.mikevidev.walkandsee;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.firebase.database.Exclude;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WalkAndSeePlace implements Comparable<WalkAndSeePlace>
{
    public String placeName;
    public String placeId;
    public LatLng placeLocation;
    public Bitmap placePhoto;
    public String placeAddress;
    public float flDistanceFromOrigin;
    public String placeDistance;
    public boolean isSelected;
    public int intPositionInRoute;

    public WalkAndSeePlace(){}

    public WalkAndSeePlace(String placeName, String placeId, LatLng placeLocation, Bitmap placePhoto,String placeAddress, float flDistanceFromOrigin, String placeDistance)
    {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeLocation = placeLocation;
        this.placePhoto = placePhoto;
        this.placeAddress = placeAddress;
        this.flDistanceFromOrigin = flDistanceFromOrigin;
        this.placeDistance = placeDistance;
        this.isSelected = false;
        this.intPositionInRoute = 0;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public LatLng getPlaceLocation() {
        return placeLocation;
    }

    public void setPlaceLocation(LatLng placeLocation) {
        this.placeLocation = placeLocation;
    }

    public Bitmap getPlacePhoto() {
        return placePhoto;
    }

    public void setPlacePhoto(Bitmap placePhoto) {
        this.placePhoto = placePhoto;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public float getFlDistanceFromOrigin() {
        return flDistanceFromOrigin;
    }

    public void setFlDistanceFromOrigin(float flDistanceFromOrigin) {this.flDistanceFromOrigin = flDistanceFromOrigin;}

    public String getPlaceDistance() {
        return placeDistance;
    }

    public void setPlaceDistance(String placeDistance) {
        this.placeDistance = placeDistance;
    }

    public boolean isSelected() {return isSelected;}

    public void setSelected(boolean selected) {isSelected = selected;}

    public int getIntPositionInRoute() {return intPositionInRoute;}

    public void setIntPositionInRoute(int intPositionInRoute) { this.intPositionInRoute = intPositionInRoute;}

    @Override
    public String toString() {
        return "Place{" +
                "placeName='" + placeName + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeLocation=" + placeLocation +
                ", placePhoto='" +  placePhoto + '\'' +
                ", placeAddress='" +  placeAddress + '\'' +
                ", placeAddress='" +  flDistanceFromOrigin + '\'' +
                '}';
    }

    @Override
    public int compareTo(WalkAndSeePlace comparePlace) {

        int intComparePlace = (int) ((WalkAndSeePlace) comparePlace).getFlDistanceFromOrigin();

        return (int) this.getFlDistanceFromOrigin() - intComparePlace;
    }

    @Exclude
    public Map<String,Object> toMapInstance()
    {
        HashMap<String,Object> mapToReturn = new HashMap<>();
        mapToReturn.put("placeName",this.placeName);
        mapToReturn.put("placeId",this.placeId);
        mapToReturn.put("placeLocation",this.placeLocation);
        //mapToReturn.put("placePhoto",this.placePhoto);
        mapToReturn.put("placeAddress",this.placeAddress);
        mapToReturn.put("flDistanceFromOrigin",this.flDistanceFromOrigin);
        mapToReturn.put("placeDistance",this.placeDistance);
        mapToReturn.put("isSelected",this.isSelected);
        mapToReturn.put("intPositionInRoute",this.intPositionInRoute);
        return mapToReturn;
    }
}
