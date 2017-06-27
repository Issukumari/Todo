package com.xpresson.android.imagefilter;

/**
 * A Filter to pixellate images.
 */
public class BlockFilter{
	
	private int blockSize = 2;

    /**
     * Construct a BlockFilter.
     */
	public BlockFilter() {
	}

    /**
     * Construct a BlockFilter.
	 * @param blockSize the number of pixels along each block edge
     */
	public BlockFilter( int blockSize ) {
		this.blockSize = blockSize;
	}

	/**
	 * Set the pixel block size.
	 * @param blockSize the number of pixels along each block edge
     * @min-value 1
     * @max-value 100+
     * @see #getBlockSize
	 */
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Get the pixel block size.
	 * @return the number of pixels along each block edge
     * @see #setBlockSize
	 */
	public int getBlockSize() {
		return blockSize;
	}

   public int[] filter( int[] src ,int W, int H ) {
      return filter(src ,W, H, null);
   }
   
	public int[] filter( int[] src ,int W, int H, int[] dst ) {
      int width = W;
      int height = H;
        
      if (dst == null) dst = new int[width * height];
		int[] pixels = new int[blockSize * blockSize];
        for ( int y = 0; y < height; y += blockSize ) {
            for ( int x = 0; x < width; x += blockSize ) {
                int w = Math.min( blockSize, width-x );
                int h = Math.min( blockSize, height-y );
                int t = w*h;
                PixelUtils.getRGB( src, x, y, w, h, width , pixels );
                int r = 0, g = 0, b = 0;
                int argb;
                int i = 0;
                for ( int by = 0; by < h; by++ ) {
                    for ( int bx = 0; bx < w; bx++ ) {
                        argb = pixels[i];
                        r += (argb >> 16) & 0xff;
                        g += (argb >> 8) & 0xff;
                        b += argb & 0xff;
                        i++;
                    }
                }
                argb = ((r/t) << 16) | ((g/t) << 8) | (b/t);
                i = 0;
                for ( int by = 0; by < h; by++ ) {
                    for ( int bx = 0; bx < w; bx++ ) {
                        pixels[i] = (pixels[i] & 0xff000000) | argb;
                        i++;
                    }
                }
                PixelUtils.setRGB( dst, x, y, w, h, width, pixels );
            }
        }
        return dst;
    }

	public String toString() {
		return "Pixellate/Mosaic...";
	}
}

