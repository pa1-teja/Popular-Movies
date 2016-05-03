package com.example.pavan.moviesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pavan.moviesapp.NetworkActivity.MovieReviewsResponse;
import com.example.pavan.moviesapp.NetworkActivity.ReviewsData;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;



public class Reviews_tab extends Fragment {

    private long movieID;

    private List<MovieReviewsResponse> movieReviewsResponses;

    private Trailers_tab trailers_tab = new Trailers_tab();
    private MainActivityFragment mainActivityFragment = new MainActivityFragment();
    private MovieReviewsAdapter movieReviewsAdapter;
    private ReviewsData reviewsData = new ReviewsData();


    private TextView no_reviews_msg;
    private ListView reviews_list_view;
    private AlertDialog.Builder builder;

    public Reviews_tab() {
        // Required empty public constructor
    }

    public static Reviews_tab newInstance(Long movieID) {
        Reviews_tab fragment = new Reviews_tab();
        Bundle args = new Bundle();
        args.putLong("movieID", movieID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieID = getArguments().getLong("movieID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews_tab, container, false);


        reviews_list_view = (ListView) view.findViewById(R.id.reviews_list_view);
        no_reviews_msg = (TextView) view.findViewById(R.id.no_reviews_msg);

        movieReviewsAdapter = new MovieReviewsAdapter(getContext());
        fetchReviewsData();

        return view;
    }

    public void fetchReviewsData() {

        Call<ReviewsData> reviewsDataCall = trailers_tab.getApi().REVIEWS_DATA_CALL(movieID, mainActivityFragment.getAPI_KEY());

        reviewsDataCall.enqueue(new Callback<ReviewsData>() {
            @Override
            public void onResponse(Response<ReviewsData> response, Retrofit retrofit) {

                System.out.println("response status reviews : " + response.isSuccess());

                reviewsData = response.body();
                movieReviewsResponses = reviewsData.getReviewsResponse();

                movieReviewsAdapter.noOfReviews = reviewsData.getReviewsResponse().size();
                System.out.println("response.body().getReviewsResponse() : " + response.body().getReviewsResponse().size());

                if (response.body().getReviewsResponse().size() == 0) {
                    no_reviews_msg.setText("No Reviews Found for this Movie");
                }


                for (MovieReviewsResponse movieReviewsResponse : movieReviewsResponses) {
                    movieReviewsAdapter.author_name.add(movieReviewsResponse.getAuthor());
                    movieReviewsAdapter.author_review.add(movieReviewsResponse.getContent());

                }
                reviews_list_view.setAdapter(movieReviewsAdapter);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("failed to fetch reviews data....");
                builder.setMessage("Sorry, We couldn't fetch the movie reviews information. Inconvenience regretted").setCancelable(false)
                        .setPositiveButton("It's Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });
    }
}