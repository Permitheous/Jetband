//Copyright (c) Microsoft Corporation All rights reserved.  
// 
//MIT License: 
// 
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
// 
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software. 
// 
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package com.microsoft.band.sdk.sampleapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.UUID;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sdk.sampleapp.tileevent.R;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.FilledButtonData;
import com.microsoft.band.tiles.pages.ScrollFlowPanel;
import com.microsoft.band.tiles.pages.TextButtonData;
import com.microsoft.band.tiles.pages.FilledButton;
import com.microsoft.band.tiles.pages.TextButton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BandTileEventAppActivity extends Activity {

	private BandClient client = null;
	private Button btnStart;
	private TextView txtStatus;
	
	private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Ab");
	private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd01");

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtStatus.setText("");
				new appTask().execute();
			}
		});
    }

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TileEvent.ACTION_TILE_OPENED);
		filter.addAction(TileEvent.ACTION_TILE_BUTTON_PRESSED);
		filter.addAction(TileEvent.ACTION_TILE_CLOSED);
		registerReceiver(messageReceiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(messageReceiver);
	}

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }


	private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {
	            TileEvent tileOpenData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
	            appendToUI("Tile open event received\n" + tileOpenData.toString()+ "\n\n");
			} else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
				TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
				System.out.print(buttonData.getElementID());
				System.out.println("TEST");
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.yahoo.com"));
				if(buttonData.getElementID() == 1) {
					myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://book.jetblue.com/B6/webqtrip.html?searchType=NORMAL&fareFamily=LOWESTFARE&numAdults=1&numChildren=0&numInfants=0&journeySpan=OW&origin=LAX&destination=BOS&departureDate=2016-02-02&source=GoogleFlights&referrerCode=GOOGLEFLIGHTS"));
				}
				else if (buttonData.getElementID() == 2){

					myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://book.jetblue.com/B6/webqtrip.html?searchType=NORMAL&fareFamily=LOWESTFARE&numAdults=1&numChildren=0&numInfants=0&journeySpan=OW&origin=LAX&destination=JFK&departureDate=2016-02-02&source=GoogleFlights&referrerCode=GOOGLEFLIGHTS"));
					}
				else if (buttonData.getElementID() == 3) {
					myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://book.jetblue.com/B6/webqtrip.html?searchType=NORMAL&fareFamily=LOWESTFARE&numAdults=1&numChildren=0&numInfants=0&journeySpan=OW&origin=LAX&destination=BTV&departureDate=2016-02-02&source=GoogleFlights&referrerCode=GOOGLEFLIGHTS"));
				}
				else if (buttonData.getElementID() == 4){
					myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://airbnb.com"));
				}


				startActivity(myIntent);

	            appendToUI("Button event received\n" + buttonData.toString()+ "\n\n");
			} else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {
				TileEvent tileCloseData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
	            appendToUI("Tile close event received\n" + tileCloseData.toString()+ "\n\n");
			}
		}
	};
	
	private class appTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					appendToUI("Band is connected.\n");
					if (addTile()) {
						updatePages();
					}
				} else {
					appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
				case DEVICE_ERROR:
					exceptionMessage = "Please make sure bluetooth is on and the band is in range.\n";
					break;
				case UNSUPPORTED_SDK_VERSION_ERROR:
					exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
					break;
				case SERVICE_ERROR:
					exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
					break;
				case BAND_FULL_ERROR:
					exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.\n";
					break;
				default:
					exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
					break;
				}
				appendToUI(exceptionMessage);
				
			} catch (Exception e) {
				appendToUI(e.getMessage());
			}
			return null;
		}
	}
	
	private void appendToUI(final String string) {
		this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	txtStatus.append(string);
            }
        });
	}
	
	private boolean doesTileExist(List<BandTile> tiles, UUID tileId) {
		for (BandTile tile:tiles) {
			if (tile.getTileId().equals(tileId)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean addTile() throws Exception {
		if (doesTileExist(client.getTileManager().getTiles().await(), tileId)) {
			return true;
		}
		
		/* Set the options */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.raw.b_icon, options);
		//Bitmap tileIcon = BitmapFactory.decodeFile("planeicon.png");
        BandTile tile = new BandTile.Builder(tileId, "Patron 2", tileIcon)
			.setPageLayouts(createButtonLayout())
			.build();
		appendToUI("Button Tile is adding ...\n");
		if (client.getTileManager().addTile(this, tile).await()) {
			appendToUI("Button Tile is added.\n");
			return true;
		} else {
			appendToUI("Unable to add button tile to the band.\n");
			return false;
		}
	}
//Original
//	private PageLayout createButtonLayout() {
//		return new PageLayout(
//				new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL)
//					.addElements(new FilledButton(0, 5, 210, 45).setMargins(0, 5, 0 ,0).setId(12).setBackgroundColor(Color.RED))
//					.addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0 ,0).setId(21).setPressedColor(Color.BLUE))
//				);
//	}
	private PageLayout createButtonLayout() {
		return new PageLayout(
				new ScrollFlowPanel(15, 0, 200, 100, FlowPanelOrientation.HORIZONTAL)
						.addElements(new TextButton(0, 5, 210, 90).setMargins(0, 5, 0, 0).setId(1).setPressedColor(Color.CYAN))
						.addElements(new TextButton(0, 0, 210, 90).setMargins(0, 5, 0 ,0).setId(2).setPressedColor(Color.CYAN))
						.addElements(new TextButton(0, 55, 210, 90).setMargins(0, 5, 0, 0).setId(3).setPressedColor(Color.CYAN))
						.addElements(new TextButton(0, 50, 210, 90).setMargins(0, 5, 0 ,0).setId(4).setPressedColor(Color.CYAN))
		);



	}
	private void updatePages() throws BandIOException {
		client.getTileManager().setPages(tileId, new PageData(pageId1, 0)
				.update(new TextButtonData(1, "Boston Flight"))
				.update(new TextButtonData(2, "NY City"))
				.update(new TextButtonData(3, "Burlington"))
				.update(new TextButtonData(4, "AIR B&B")));
		appendToUI("Send button page data to tile page \n\n");
	}
	
	private boolean getConnectedBandClient() throws InterruptedException, BandException {
		if (client == null) {
			BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
			if (devices.length == 0) {
				appendToUI("Band isn't paired with your phone.\n");
				return false;
			}
			client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
		} else if (ConnectionState.CONNECTED == client.getConnectionState()) {
			return true;
		}
		
		appendToUI("Band is connecting...\n");
		return ConnectionState.CONNECTED == client.connect().await();
	}
}

