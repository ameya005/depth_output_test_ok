
#include <string.h>
#include <jni.h>
#include <android/log.h>

static const char *TAG = "inputdevices";
#define LOGI(fmt, args...) __android_log_print (ANDROID_LOG_INFO, TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print (ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print (ANDROID_LOG_ERROR, TAG, fmt, ##args)


#define MAX_DEPTH 10000
float DepthHist[MAX_DEPTH];

void DrawDepth( int* pDepth_value, int width, int height )
{

	int* pView = pDepth_value;
	// Calculate the accumulative histogram (the yellow display...)
	memset(DepthHist, 0, MAX_DEPTH*sizeof(float));

	int nNumberOfPoints = 0;
	int x,y;
	for (y = 0; y < height; ++y)
	{
		for ( x = 0; x < width; ++x, ++pView)
		{
			if (*pView != 0)
			{
				DepthHist[*pView]++;
				nNumberOfPoints++;
			}
		}
	}


	int nIndex;
	for (nIndex=1; nIndex < MAX_DEPTH; nIndex++)
	{
		DepthHist[nIndex] += DepthHist[nIndex-1];
	}
	if (nNumberOfPoints)
	{
		for (nIndex=1; nIndex<MAX_DEPTH; nIndex++)
		{
			DepthHist[nIndex] = (256 * (1.0f - (DepthHist[nIndex] / nNumberOfPoints)));
//			LOGD("depthhist_%f",DepthHist[nIndex]);
		}
	}


	pView = pDepth_value;

	for (y = 0; y < height; ++y)
	{

		for (x = 0; x < width; ++x)
		{
			if (*pView != 0)
			{
				int nHistValue = DepthHist[*pView];


				pDepth_value[width*y+x] =  nHistValue << 16;
				pDepth_value[width*y+x] |= nHistValue << 8;
				pDepth_value[width*y+x] |= 255 << 24;

//				*((unsigned char*)(pDepth_value + width*y+x) + 0) = 123;
//				*((unsigned char*)(pDepth_value + width*y+x) + 1)=  nHistValue;
//				*((unsigned char*)(pDepth_value + width*y+x) + 2)= 2;
//				*((unsigned char*)(pDepth_value + width*y+x) + 3)= 100;

			}
			pView++;
		}
	}
}


void DrawDepth_java( int* pDepth_value, int width, int height )
{

	int x,y;
	for(y = 0;y < height;y++)
	{
		for(x = 0;x< width;x++)
		{
			int depth_value = 0;
			//depth_value 为实际每个像素的深度值
			depth_value = pDepth_value[width*y+x];
			pDepth_value[width*y+x] = ((int) (depth_value * 0.0256)) << 16;
			pDepth_value[width*y+x] |= ((int) (depth_value * 0.0256)) << 8;
			pDepth_value[width*y+x] |= 255 << 24;

			//pixels[getDepthData.getXRes()*y+x] = 0xffffffff;

			//Log.v("test_output", Integer.toString(depth_value));
		}
	}

}

jboolean Java_com_example_asusdepthoutput_MainActivity_CoventFromDepthTORGB( JNIEnv* env,jobject thiz ,jintArray pDepth,jint width,jint height)
{
	jint * arr;
	arr = (*env)->GetIntArrayElements(env,pDepth,NULL);

	DrawDepth(arr, width, height );

	(*env)->ReleaseIntArrayElements(env,pDepth, arr, 0);


//	LOGD("end");
	return 1;




}





