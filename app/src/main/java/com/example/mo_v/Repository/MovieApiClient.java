package com.example.mo_v.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.mo_v.AppExecutors;
import com.example.mo_v.models.MovieModel;
import com.example.mo_v.request.Servicey;
import com.example.mo_v.response.MovieSearchResponse;
import com.example.mo_v.utils.Credentials;
import com.example.mo_v.utils.MovieApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {

    //Global Request
    private  RetrieveMoviesRunnable retrieveMoviesRunnable;
    //for live data
    private MutableLiveData<List<MovieModel>> mMovies;

    private static MovieApiClient instance;

    //for pop movie
    private MutableLiveData<List<MovieModel>>mMoviePop;
    private  RetrieveMoviesRunnablePop retrieveMoviesRunnablePop;


    public static MovieApiClient getInstance(){
        if(instance==null){
            instance=new MovieApiClient();

        }
        return instance;
    }

    public MovieApiClient() {
        mMovies=new MutableLiveData<>();
        mMoviePop=new MutableLiveData<>();
    }

    public MutableLiveData<List<MovieModel>> getmMovies() {
        return mMovies;
    }
    public MutableLiveData<List<MovieModel>> getmMoviesPop() {
        return mMoviePop;
    }





    public void searchMovieApiPop(int pageenumbeer) {

        if(retrieveMoviesRunnablePop!=null)
        {
            retrieveMoviesRunnablePop=null;
        }

        retrieveMoviesRunnablePop=new RetrieveMoviesRunnablePop(pageenumbeer);

        final Future myHandler2 = AppExecutors.getInstance().getmNetworkIO().submit(retrieveMoviesRunnablePop);

        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //for cutting the flow
                myHandler2.cancel(true);
            }
        }, 1000, TimeUnit.MILLISECONDS);

    }

    public void searchMovieApi(String queryy,int pageenumbeer) {

        if(retrieveMoviesRunnable!=null)
        {
            retrieveMoviesRunnable=null;
        }

        retrieveMoviesRunnable=new RetrieveMoviesRunnable(queryy,pageenumbeer);

        final Future myHandler = AppExecutors.getInstance().getmNetworkIO().submit(retrieveMoviesRunnable);

        AppExecutors.getInstance().getmNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //for cutting the flow
                myHandler.cancel(true);
            }
        }, 300, TimeUnit.MILLISECONDS);

    }


    private class RetrieveMoviesRunnable implements Runnable{

        private String query ;
        private int pageNumber;
        boolean cancelRequest;

            public RetrieveMoviesRunnable(String query, int pageNumber) {
                this.query = query;
                this.pageNumber = pageNumber;

                cancelRequest=false;

            }

            @Override
            public void run() {

                try {

                    Response response=getMovies(query,pageNumber).execute();
                    if (cancelRequest)
                    {
                        return;
                    }
                    if(response.code()==200){

                    List<MovieModel>list=new ArrayList<>(((MovieSearchResponse)response.body()).getMovies());

                    if(pageNumber==1){
                        //sending data to live data
                        //postvalue;used for background thread;
                        // set value: not for background thread;
                        mMovies.postValue(list);
                    }
                    else {

                        List <MovieModel> currentMovies=mMovies.getValue();
                        currentMovies.addAll(list);
                        mMovies.postValue(currentMovies);
                    }


                    }
                    else{

                        String error=response.errorBody().string();
                        Log.v("Tag","Error"+error);
                        mMovies.postValue(null);

                    }


                    } catch (IOException e) {
                         e.printStackTrace();
                         mMovies.postValue(null);
                    }



                }
                    //search Methode

                  private Call<MovieSearchResponse>getMovies(String Query, int pageNumber){

                      return Servicey.getMovieApi().searchmovie(Credentials.API_Key,Query,pageNumber);

                   }
                  private void cancelRequest(){
                      Log.v("Tag","Cancel Serach Request ");

                      cancelRequest=true;

                }

            }

    private class RetrieveMoviesRunnablePop implements Runnable{


        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnablePop( int pageNumber) {

            this.pageNumber = pageNumber;

            cancelRequest=false;

        }

        @Override
        public void run() {

            try {

                Response response2=getpop(pageNumber).execute();
                if (cancelRequest)
                {
                    return;
                }
                if(response2.code()==200){

                    List<MovieModel>list=new ArrayList<>(((MovieSearchResponse)response2.body()).getMovies());

                    if(pageNumber==1){
                        //sending data to live data
                        //postvalue;used for background thread;
                        // set value: not for background thread;
                         mMoviePop.postValue(list);
                    }
                    else {

                        List <MovieModel> currentMovies=mMovies.getValue();
                        currentMovies.addAll(list);
                        mMoviePop.postValue(currentMovies);
                    }


                }
                else{

                    String error=response2.errorBody().string();
                    Log.v("Tag","Error"+error);
                    mMoviePop.postValue(null);

                }


            } catch (IOException e) {
                e.printStackTrace();
                mMovies.postValue(null);
            }



        }
        //get pop

        private Call<MovieSearchResponse>getpop( int pageNumberr){

           return Servicey.getMovieApi().getPopular(
                   Credentials.API_Key,
                   pageNumberr
           );

        }
        private void cancelRequest(){
            Log.v("Tag","Cancel Serach Request ");
            cancelRequest=true;

        }

    }




}





