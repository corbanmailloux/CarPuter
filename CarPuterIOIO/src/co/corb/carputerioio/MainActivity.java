package co.corb.carputerioio;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.SVBar;

public class MainActivity extends IOIOActivity {

	private ColorPicker colorPicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
		colorPicker.addSVBar((SVBar) findViewById(R.id.svbar));
		
		colorPicker.setOldCenterColor(invertColorKeepOpacity(colorPicker.getColor()));
		colorPicker.setOnColorChangedListener(new OnColorChangedListener() {

			@Override
			public void onColorChanged(int color) {
				// container.setBackgroundColor(color);
				colorPicker.setOldCenterColor(invertColorKeepOpacity(color));
			}
		});
	}

	private int invertColorKeepOpacity(int color) {
		return ((~color) | (color & 0xFF000000));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private PwmOutput redLed;
		private PwmOutput greenLed;
		private PwmOutput blueLed;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.IOIOLooper#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			toast("IOIO connected!");
			redLed = ioio_.openPwmOutput(12, 80000);
			greenLed = ioio_.openPwmOutput(13, 80000);
			blueLed = ioio_.openPwmOutput(14, 80000);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * @throws InterruptedException
		 *             When the IOIO thread has been interrupted.
		 * 
		 * @see ioio.lib.util.IOIOLooper#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			redLed.setDutyCycle(Color.red(colorPicker.getColor()) / 255f);
			greenLed.setDutyCycle(Color.green(colorPicker.getColor()) / 255f);
			blueLed.setDutyCycle(Color.blue(colorPicker.getColor()) / 255f);
			Thread.sleep(20);
		}

		/**
		 * Called when the IOIO is disconnected.
		 * 
		 * @see ioio.lib.util.IOIOLooper#disconnected()
		 */
		@Override
		public void disconnected() {
			toast("IOIO disconnected");
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void toast(final String message) {
		final Context context = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
