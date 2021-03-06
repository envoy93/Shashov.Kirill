package com.transportsmr.app.utils;

import com.transportsmr.app.model.DaoSession;
import com.transportsmr.app.model.Stop;
import com.transportsmr.app.model.StopDao;
import retrofit2.Response;

import java.util.HashSet;
import java.util.List;

/**
 * Created by kirill on 28.12.16.
 */
public class StopsClassifierUpdater extends ClassifierUpdater {

    public StopsClassifierUpdater(DaoSession daoSession, ToSamaraApi api) {
        super(daoSession, api);
    }

    @Override
    public String getFileName() {
        return Constants.SHARED_STOPS_FILENAME;
    }

    @Override
    public boolean update() {
        Response<ToSamaraApi.StopsResponse> response = null;
        try {
            response = getApi().getStops().execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (response.body() == null) {
            return false;
        }

        List<Stop> stops = response.body().getStops();
        if (stops != null && !stops.isEmpty()) {
            HashSet<String> favorites = new HashSet<String>();
            List<Stop> favList = getDaoSession().getStopDao().queryBuilder().where(StopDao.Properties.Favorite.eq(true)).build().list();
            for (Stop favStop : favList) {
                favorites.add(favStop.getKs_id());
            }

            for (Stop stop : stops) {  // :(
                if (stop.getTitle() == null) {
                    stop.setTitle("");
                }
                if (stop.getAdjacentStreet() == null) {
                    stop.setAdjacentStreet("");
                }
                if (stop.getDirection() == null) {
                    stop.setDirection("");
                }
                if (stop.getLongitude() == null) {
                    stop.setLongitude(0.0f);
                }
                if (stop.getLatitude() == null) {
                    stop.setLatitude(0.0f);
                }
                stop.setTitle_lc(stop.getTitle().toLowerCase());
                stop.setAdjacentStreet_lc(stop.getAdjacentStreet().toLowerCase());
                stop.setFavorite(favorites.contains(stop.getKs_id()));
            }

            getDaoSession().getStopDao().deleteAll();
            getDaoSession().getStopDao().insertInTx(stops);
            return true;
        }
        return false;
    }

    /*@Override
    protected List<Stop> parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        Stop stop = null;
        String text = "";
        StopDao stopDao = getDaoSession().getStopDao();
        HashSet<String> favorites = new HashSet<String>();
        List<Stop> favList = stopDao.queryBuilder().where(StopDao.Properties.Favorite.eq(true)).build().list();
        for (Stop favStop : favList) {
            favorites.add(favStop.getKs_id());
        }
        ArrayList<Stop> stops = new ArrayList<Stop>();

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagname = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagname.equalsIgnoreCase("stop")) {
                        stop = new Stop();
                    }
                    text = "";
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText().trim();
                    break;

                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("stop")) {
                        stops.add(stop);
                    } else if (tagname.equalsIgnoreCase("KS_ID")) {
                        stop.setKs_id(text);
                        stop.setFavorite(favorites.contains(text));
                    } else if (tagname.equalsIgnoreCase("title")) {
                        stop.setTitle(text);
                        stop.setTitle_lc(text.toLowerCase());
                    } else if (tagname.equalsIgnoreCase("adjacentStreet")) {
                        stop.setAdjacentStreet(text);
                        stop.setAdjacentStreet_lc(text.toLowerCase());
                    } else if (tagname.equalsIgnoreCase("direction")) {
                        stop.setDirection(text);
                    } else if (tagname.equalsIgnoreCase("latitude")) {
                        if (text.isEmpty()) {
                            stop.setLatitude(0.0f);
                        } else {
                            stop.setLatitude(Float.parseFloat(text));
                        }
                    } else if (tagname.equalsIgnoreCase("longitude")) {
                        if (text.isEmpty()) {
                            stop.setLongitude(0.0f);
                        } else {
                            stop.setLongitude(Float.parseFloat(text));
                        }
                    } else if (tagname.equalsIgnoreCase("busesMunicipal")) {
                        stop.setBusesMunicipal(text);
                    } else if (tagname.equalsIgnoreCase("busesCommercial")) {
                        stop.setBusesCommercial(text);
                    } else if (tagname.equalsIgnoreCase("busesPrigorod")) {
                        stop.setBusesPrigorod(text);
                    } else if (tagname.equalsIgnoreCase("busesSeason")) {
                        stop.setBusesSeason(text);
                    } else if (tagname.equalsIgnoreCase("busesSpecial")) {
                        stop.setBusesSpecial(text);
                    } else if (tagname.equalsIgnoreCase("trams")) {
                        stop.setTrams(text);
                    } else if (tagname.equalsIgnoreCase("trolleybuses")) {
                        stop.setTrolleybuses(text);
                    } else if (tagname.equalsIgnoreCase("metros")) {
                        stop.setMetros(text);
                    } else {
                    }
                    break;

                default:
                    break;
            }
            eventType = parser.next();
        }
        return stops;
    }

    @Override
    protected void update(List<Stop> list) {
        getDaoSession().getStopDao().deleteAll();
        getDaoSession().getStopDao().insertInTx(list);
    }*/

}
