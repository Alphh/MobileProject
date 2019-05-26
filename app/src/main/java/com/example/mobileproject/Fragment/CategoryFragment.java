package com.example.mobileproject.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobileproject.CategoryModel.CategoryItem;
import com.example.mobileproject.Common.Common;
import com.example.mobileproject.Interface.itemClickListener;
import com.example.mobileproject.ListWallpaper;
import com.example.mobileproject.R;
import com.example.mobileproject.ViewHolder.CategoryViewHolder;
import com.example.mobileproject.WriteImages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryFragment extends Fragment {

    private static CategoryFragment INSTANCE = null;
    //contains details = url & name used by class called WriteImages
    private final ArrayList<String[]> image_details;
    //contains the bitmap
    private final ArrayList<ImageView> image_array;
    //these are the results
    private final RealmResults<CategoryItem> result;
    //setting up firebase & recycler viewer
    private FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> adapter;
    private RecyclerView.Adapter<CategoryViewHolder> internal_adapter;
    private RecyclerView recyclerView;
    //this is used for check_internet
    private AlertDialog internetDialog;

    public CategoryFragment()
    {
        image_details = new ArrayList<>();
        image_array = new ArrayList<>();

        //Checking local realm
        Realm.init(Common.APPLICATION_CONTEXT);
        Realm realm = Realm.getDefaultInstance();
        result = realm.where(CategoryItem.class).findAll().sort("category_id");

        //If realm has no images stored, check online, if not, get them from local DB
        if (result.size() < 4) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference categoryBackground = database.getReference(Common.STR_CATEGORY_BACKGROUND);

            FirebaseRecyclerOptions<CategoryItem> options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                    .setQuery(categoryBackground, CategoryItem.class)
                    .build();


            adapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
                //Here we are setting where the item falls withing the recyclerview
                @Override
                protected void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position, @NonNull final CategoryItem model) {
                    //Picasso is again used to get image links and place them as images
                    Picasso.get()
                            .load(model.getImageLink())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(holder.background_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    image_details.add(new String[]{model.getName(), model.getImageLink(), holder.getAdapterPosition() + ""});
                                    image_array.add(holder.background_image);
                                }

                                @Override
                                public void onError(Exception e) {
                                    //Try Again online.
                                    Picasso.get()
                                            .load(model.getImageLink())
                                            .error(R.drawable.ic_cloud_upload_black_24dp)
                                            .into(holder.background_image, new Callback() {

                                                //if we get a result from the DB(image), we add info in the array and the link is shortened
                                                @Override
                                                public void onSuccess() {
                                                    image_details.add(new String[]{model.getName(), model.getImageLink(), holder.getAdapterPosition() + ""});
                                                    image_array.add(holder.background_image);

                                                    if (image_array.size() == 4) {
                                                        for (String[] image : image_details) {
                                                            //Shortening image URL to E.g. "apple-2924531_960_720.jpg"
                                                            image[1] = new StringBuilder(image[1]).reverse().toString();
                                                            String parts[] = image[1].split("/");
                                                            image[1] = parts[0];
                                                            image[1] = new StringBuilder(image[1]).reverse().toString(); //apple-2924531_960_720.jpg
                                                        }

                                                        if (result.size() == 0) {
                                                            new WriteImages(image_array, image_details).execute();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Log.e("ERROR,", "Could not fetch image");

                                                }
                                            });
                                }
                            });

                    //if the holder is clicked, we list all the wallpapers having the same category ID
                    holder.category_name.setText(model.getName());
                    holder.setItemClickListener(new itemClickListener() {
                        @Override
                        public void onClick(int position) {
                            //passing category
                            Common.CATEGORY_ID_SELECTED = adapter.getRef(position).getKey();
                            Common.CATEGORY_SELECTED = model.getName();
                            //start intent
                            Intent intent = new Intent(getActivity(), ListWallpaper.class);
                            startActivity(intent);
                        }
                    });
                }
                @NonNull
                @Override
                public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View itemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_category, viewGroup, false);

                    return new CategoryViewHolder(itemView);
                }
            };

        }

        //if we have images stored locally, we load them locally
        else {
            setInternalAdapter();
        }
    }

    // default for all fragments
    public static CategoryFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CategoryFragment();
        return INSTANCE;
    }

    private void setInternalAdapter() {
        internal_adapter = new RecyclerView.Adapter<CategoryViewHolder>() {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_category, viewGroup, false);

                return new CategoryViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int i) {
                try {
                    String temp = new StringBuilder(result.get(i).getImageLink()).reverse().toString();
                    String parts[] = temp.split("/");
                    temp = parts[0];
                    temp = new StringBuilder(temp).reverse().toString(); //apple-2924531_960_720.jpg

                    //Images are stored as bitmaps
                    final Bitmap b = BitmapFactory
                            .decodeStream(new FileInputStream(Common.APPLICATION_CONTEXT.getDir("images", Context.MODE_PRIVATE)
                                    + "/" + temp));

                    holder.background_image.setImageBitmap(b);
                    holder.category_name.setText(result.get(i).getName());


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                holder.setItemClickListener(new itemClickListener() {
                    @Override
                    public void onClick(int position) {
                        int number = holder.getAdapterPosition() + 1;
                        Common.CATEGORY_ID_SELECTED = "0" + number;
                        Common.CATEGORY_SELECTED = result.get(holder.getAdapterPosition()).getName();
                        if (Common.hasInternetConnectivity(getActivity())) {
                            Intent intent = new Intent(getActivity(), ListWallpaper.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "No Internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return result.size();
            }

        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recycler_category);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        internal_adapter = new RecyclerView.Adapter<CategoryViewHolder>() {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };

        setCategory();
        //dialog for checking internet
        internetDialog = new AlertDialog.Builder(getActivity()).create();
        checkInternet();
        return view;
    }
    //here the activity life cycle starts
    private void setCategory() {
        if (result.size() == 4) {
            if (internal_adapter.getItemCount() < 4) {
                setInternalAdapter();
                recyclerView.setAdapter(internal_adapter);
            } else {
                recyclerView.setAdapter(internal_adapter);
            }
        } else {
            adapter.startListening();
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!Common.hasInternetConnectivity(getActivity())) {
            recyclerView.setAdapter(internal_adapter);
        } else {
            setCategory();
        }

        checkInternet();
    }

    @Override
    public void onStop() {
        if (adapter != null)
            adapter.stopListening();

        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Common.hasInternetConnectivity(getActivity())) {
            recyclerView.setAdapter(internal_adapter);
        } else {
            setCategory();
        }

        checkInternet();
    }

    //checking if there is internet connection, if not, we pass messages to the user notifiying him of app limitations
    private void checkInternet() {
        if (!Common.hasInternetConnectivity(getActivity())) {
            String message;

            if (!internetDialog.isShowing()) {
                if (result.size() == 0) {
                    message = "Internet Access is required when opening this app for the first time";

                    internetDialog = new AlertDialog.Builder(getActivity()).setTitle("No Internet Access").setMessage(message)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            }).create();

                    internetDialog.show();

                } else {
                    message = "App functionality will be limited.";

                    internetDialog = new AlertDialog.Builder(getActivity()).setTitle("No Internet Access").setMessage(message)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Common.hasInternetConnectivity(getActivity())) {
                                        dialog.dismiss();
                                    }
                                }
                            }).create();
                    internetDialog.show();
                }
            }
        }
    }
}
