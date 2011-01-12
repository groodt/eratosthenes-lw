package	groodt.eratosthenes;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class EratosthenesWallpaper extends WallpaperService {

	//receive callbacks from PrimesThread.
    private final Handler handler = new Handler();
    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
    	android.os.Debug.waitForDebugger();
        return new PrimesEngine();
    }
    
    class PrimesEngine extends Engine {

    	private boolean visible;
        private final Runnable drawingThread = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        private final Paint paint;
        
        private EratosthenesPrimesStepper primesStepper = new EratosthenesPrimesStepper(120);
        
        private int[] colors = {Color.RED, Color.BLUE, Color.GREEN};
        
        private List<Result> results = new ArrayList<Result>();
        
        PrimesEngine() {
        	paint = new Paint();
        	paint.setColor(Color.BLACK);
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawingThread);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                drawFrame();
            } else {
                handler.removeCallbacks(drawingThread);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawingThread);
        }

		void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // draw something
                	c.drawColor(Color.WHITE);
                	drawNumberGrid(c);
                	drawNumbers(c);
                    stepThroughPrimes();
                    drawResults(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw in 1s time
            handler.removeCallbacks(drawingThread);
            if (visible) {
                handler.postDelayed(drawingThread, 1000);
            }
        }

		private void drawResults(Canvas c) {
			for(Result result:results) {
				switch(result.getType()) {
					case 1:
						drawPrime(c, result.n);
						break;
					case 0:
						drawMultiple(c, result.n);
						break;
				}
			}
		}

		//might help sort out cells for drawing into
        private void drawNumberGrid(Canvas c) {
			int width = c.getWidth();
			int height = c.getHeight();
			float colWidth = width/10f;
			float rowHeight = height/12f;
			for (int i=1; i<10; i++) {
				c.drawLine(colWidth*i, 0, colWidth*i, height, paint);
			}
			for (int j=1; j<12; j++) {
				c.drawLine(0, rowHeight*j, width, rowHeight*j, paint);
			}
		}
        
        private void drawNumbers(Canvas c) {
			int width = c.getWidth();
			int height = c.getHeight();
			float colWidth = width/10f;
			float rowHeight = height/12f;
			int cell = 0;
			for (int row=1; row<=12; row++) {
					for (int column=1; column<=10; column++ ) {
						cell = column + ((row-1)*10);
						if (cell == 1) continue;
						c.drawText(String.valueOf(cell), colWidth*column-colWidth, rowHeight*row, paint);
					}
			}
		}

		void stepThroughPrimes() {
            //drawIntegers(c);
            primesStepper.step();
            int op = primesStepper.getOp();
            switch(op) {
            	case -1:
            		results.clear();
            		break;
            	case 1:
            		int prime = primesStepper.getIndex();
            		results.add(new Result(prime, 1));
            		break;
            	case 0:
            		int multiple = primesStepper.getMultiple();
            		results.add(new Result(multiple, 0));
            		break;
            }
        }
                
        void drawPrime(Canvas c, int n) {
			int width = c.getWidth();
			int height = c.getHeight();
			float colWidth = width/10f;
			float rowHeight = height/12f;
			float x = n%10 == 0 ? 10 : (n%10);
			float y = n%10 == 0 ? (n-1) /10 : (n/10);
			c.drawCircle(x*colWidth-colWidth+10, y*rowHeight+rowHeight-10, 10f, paint);
        }
        
        void drawMultiple(Canvas c, int n) {
			int width = c.getWidth();
			int height = c.getHeight();
			float colWidth = width/10f;
			float rowHeight = height/12f;
			float x = n%10 == 0 ? 10 : (n%10);
			float y = n%10 == 0 ? (n-1) /10 : (n/10);
			c.drawRect(x*colWidth-colWidth, y*rowHeight+rowHeight-20, x*colWidth-colWidth+20, y*rowHeight+rowHeight, paint);
        }
        
        void drawIntegers(Canvas c) {
        	String[] rows = createIntegerRows();
        	int offset = 100;
        	for (String row:rows) {
        		c.drawText(row, 0, offset, paint);
        		offset+=10;
        	}
        }
        
        private String[] createIntegerRows() {
        	String[] rows = new String[10];
        	String s = "";
        	for (int i=1, row=0; i <= 100; i++) {
        		if (i == 1) {
        			s+="  ";
        		} else if (i <= 10) {
        			s+=" " + i + " ";
        		}
        		else {
        			s+=i + " ";
        		}
        		if (i % 10 == 0) {
        			rows[row]=s;
        			row++;
        			s="";
        		}
        	}
        	return rows;
        }
                
//        private Runnable timerTask = new Runnable() {
//			public void run() {
//				final SurfaceHolder holder = getSurfaceHolder();
//				Canvas c = null;
//				try {
//					c = holder.lockCanvas();
//					if (c != null) {
//						// draw something
//						c.drawColor(Color.WHITE);
//						Random r = new Random();
//						int x = r.nextInt(100);
//						int y = r.nextInt(100);
//						c.drawCircle(x, y, 10, paint);
//					}
//				} finally {
//					if (c != null)
//						holder.unlockCanvasAndPost(c);
//				}
//				//handler.postDelayed(this, 1000);
//     	   }
//     	};
    }
    
    static class Result {
    	private final int n;
    	private final int type;
    	
    	public Result(int n, int type) {
    		this.n=n;
    		this.type=type;
    	}

		public int getN() {
			return n;
		}

		public int getType() {
			return type;
		}
    }
}
