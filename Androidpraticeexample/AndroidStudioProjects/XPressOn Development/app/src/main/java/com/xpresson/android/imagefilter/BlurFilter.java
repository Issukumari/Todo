/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.xpresson.android.imagefilter;

import android.graphics.Matrix;

/**
 * A simple blur filter. You should probably use BoxBlurFilter instead.
 */
public class BlurFilter extends ConvolveFilter {
 	
 	/**
     * A 3x3 convolution kernel for a simple blur.
     */
    protected static float[] blurMatrix = {
		1/14f, 2/14f, 1/14f,
		2/14f, 2/14f, 2/14f,
		1/14f, 2/14f, 1/14f
	};

	public BlurFilter() {
		super( blurMatrix );
	}
	
	public void setBlurMatrix(int numOfTimes)
	{
	   if (numOfTimes <= 1) return;
	   Matrix origMat = new Matrix(), newMat = new Matrix();
	   origMat.setValues(blurMatrix); newMat.setValues(blurMatrix);
	   for (int i = 1; i < numOfTimes; i++) newMat.preConcat(origMat);
	   float[] newBlurMat = new float[9]; newMat.getValues(newBlurMat);
	   setKernel(new Kernel(3, 3, newBlurMat));
	}

	public String toString() {
		return "Blur/Simple Blur";
	}
}
