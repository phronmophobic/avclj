#include <iostream>
#include "libavclj.h"

using namespace std;



void make_frame(char* frame_data, int height, int width, int n_channels, int frame) {
  int linesize = width * n_channels;
  for (int h = 0; h < height; ++h) {
    for (int w = 0; w < width; ++w) {
      int y = ((h + frame) / 32) % 2;
      int x = ((w + frame) / 32) % 2;
      int charval = (y == 0 && x == 0) ? 255 : 0;
      for(int c = 0; c < n_channels; ++ c) {
	frame_data[h*linesize + w*n_channels + c] = charval;
      }
    }
  }
}


int main(int c, char** v) {
  graal_isolate_t *isolate = NULL;
  graal_isolatethread_t *thread = NULL;

  if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
    fprintf(stderr, "initialization error\n");
    return 1;
  }
  cout << "initialized? " << is_avclj_initialized(thread) << endl;
  initialize_avclj(thread);
  cout << "initialized? " << is_avclj_initialized(thread) << endl;
  long encoder = make_h264_encoder(thread, 256, 256,
				   (void*) "libavclj.mp4",
				   (void*) "AV_PIX_FMT_RGB24");
  cout << "got encoder: " << encoder << endl;
  int n_bytes = 256 * 256 * 3;
  char* image = (char*)malloc(n_bytes);
  for( int frame = 0; frame < 300; ++frame ) {
    make_frame(image, 256, 256, 3, frame);
    encode_frame(thread, encoder, image, n_bytes);
  }
  close_encoder(thread, encoder);
  return 0;
}
