(ns avclj.avformat
  (:require [tech.v3.datatype.ffi :as dt-ffi]
            [avclj.avcodec :refer [check-error]]
            [avclj.av-context :as av-context]
            [tech.v3.resource :as resource])
  (:import [tech.v3.datatype.ffi Pointer]
           [java.util Map]))


(def avformat-def
  {:avformat_version {:rettype :int32
                      :doc "version of the library"}
   :avformat_configuration {:rettype :string
                            :doc "Build configuration of the library"}
   :avformat_license {:rettype :string
                      :doc "License of the library"}
   :avformat_open_input {:rettype :int32
                         :argtypes [['ps :pointer]  ;;AVFormatContext**
                                    ['url :string]
                                    ['av-input-format :pointer?] ;;AVInputFormat*
                                    ['options :pointer?]]         ;;AVDictionary**
                         :doc "Open an input stream and read the header. The codecs are not opened.
The stream must be closed with avformat_close_input.
See https://github.com/FFmpeg/FFmpeg/blob/master/libavformat/avformat.h#L1966"}
   :avformat_find_stream_info {:rettype :int32
                               :argtypes [['ic :pointer]     ;;AVFormatContext *
                                          ['options :pointer?]] ;;AVDictionary **
                               :doc "Read packets of a media file to get stream information. This
is useful for file formats with no headers such as MPEG. This
function also computes the real framerate in case of MPEG-2 repeat
frame mode.
The logical file position is not changed by this function;
examined packets may be buffered for later processing.
See https://github.com/FFmpeg/FFmpeg/blob/master/libavformat/avformat.h#L1988"}
   :av_find_best_stream {:rettype :int32
                         :argtypes [['ic :pointer] ;;AVFormatContext *,
                                    ['type :int32] ;;enum AVMediaType ,
                                    ['wanted_stream_nb :int32]
                                    ['related_stream :int32] ;;int ,
                                    ['decoder_ret :pointer] ;;const AVCodec **decoder_ret,
                                    ['flags :int32]] ;;unused
                         :doc "Find the \"best\" stream in the file.
The best stream is determined according to various heuristics as the most
likely to be what the user expects.
If the decoder parameter is non-NULL, av_find_best_stream will find the
default decoder for the stream's codec; streams for which no decoder can
be found are ignored.

Return the non-negative stream number in case of success,
AVERROR_STREAM_NOT_FOUND if no stream with the requested type
could be found,
AVERROR_DECODER_NOT_FOUND if streams were found but no decoder

See https://github.com/FFmpeg/FFmpeg/blob/master/libavformat/avformat.h#L2025"}
   :av_read_frame {:rettype :int32
                   :argtypes [['s :pointer] ;;AVFormatContext *s
                              ['pkt :pointer]] ;;AVPacket *
                   :doc "Return the next frame of a stream.
This function returns what is stored in the file, and does not validate
that what is there are valid frames for the decoder. It will split what is
stored in the file into frames and return one for each call. It will not
omit invalid data between valid frames so as to give the decoder the maximum
information possible for decoding.

On success, the returned packet is reference-counted (pkt->buf is set) and
valid indefinitely. The packet must be freed with av_packet_unref() when
it is no longer needed. For video, the packet contains exactly one frame.
For audio, it contains an integer number of frames if each frame has
a known fixed size (e.g. PCM or ADPCM data). If the audio frames have
a variable size (e.g. MPEG audio), then it contains one frame.

pkt->pts, pkt->dts and pkt->duration are always set to correct
values in AVStream.time_base units (and guessed if the format cannot
provide them). pkt->pts can be AV_NOPTS_VALUE if the video format
has B-frames, so it is better to rely on pkt->dts if you do not
decompress the payload.

return 0 if OK, < 0 on error or end of file. On error, pkt will be blank
(as if it came from av_packet_alloc())."}
   :avformat_close_input {:rettype :void
                          :argtypes [['s :pointer]] ;;AVFormatContext **
                          :doc "Close an input stream.
See https://github.com/FFmpeg/FFmpeg/blob/master/libavformat/avformat.h#L2160"}

   :avformat_alloc_output_context2 {:rettype :int32
                                    :argtypes [['ctx :pointer]
                                               ['oformat :pointer?]
                                               ;;one or the other of format_name
                                               ;;or file_name may be used.
                                               ['format_name :pointer?]
                                               ['file_name :pointer?]]
                                    :check-error? true
                                    :doc "Open an output context"}
   :avformat_free_context {:rettype :void
                           :argtypes [['ctx :pointer]]
                           :doc "Free an avformat context"}
   :avformat_new_stream {:rettype :pointer
                         :argtypes [['avfmt-ctx :pointer]
                                    ['codec :pointer]]
                         :doc "Create a new stream"}
   :av_write_frame {:rettype :int32
                    :argtypes [['avfmt-ctx :pointer]
                               ['packet :pointer]]
                    :check-error? true
                    :doc "Write a packet to the output frame.  Does not own packet"}
   :av_interleaved_write_frame {:rettype :int32
                                :argtypes [['avfmt-ctx :pointer]
                                           ['packet :pointer]]
                                :check-error? true
                                :doc "Write a packet to the output frame.  Owns packet"}
   :avformat_write_header {:rettype :int32
                           :argtypes [['avfmt-ctx :pointer]
                                      ['options-dict-pptr :pointer?]]
                           :check-errors? true
                           :doc "Write the file header"}
   :av_write_trailer {:rettype :int32
                      :argtypes [['avfmt-ctx :pointer]]
                      :check-errors? true
                      :doc "Write the file trailer"}
   :avio_open2 {:rettype :int32
                :argtypes [['ctx :pointer]
                           ['url :string]
                           ['flags :int32]
                           ['int_cp :pointer?]
                           ['options :pointer?]]
                :check-errors? true
                :doc "Open an io pathway"}
   :avio_close {:rettype :int32
                :argtypes [['s :pointer]]
                :check-errors? true
                :doc "Close the avio context"}
   :avio_closep {:rettype :int32
                 :argtypes [['s-ptr :pointer]]
                 :check-errors? true
                 :doc "Close a pointer to a pointer to the avio context"}

   ;; int avformat_seek_file(AVFormatContext *s, int stream_index, int64_t min_ts, int64_t ts, int64_t max_ts, int flags);
   :avformat_seek_file {:rettype :int32
                        :argtypes [['s-ptr :pointer]
                                   ['stream-index :int32]
                                   ['min_ts :int64]
                                   ['ts :int64]
                                   ['max_ts :int64]
                                   ['flags :int32]]
                        :check-errors? true
                        :doc "Seek to timestamp ts.
Seeking will be done so that the point from which all active streams
can be presented successfully will be closest to ts and within min/max_ts.
Active streams are all streams that have AVStream.discard < AVDISCARD_ALL.

If flags contain AVSEEK_FLAG_BYTE, then all timestamps are in bytes and
are the file position (this may not be supported by all demuxers).
If flags contain AVSEEK_FLAG_FRAME, then all timestamps are in frames
in the stream with stream_index (this may not be supported by all demuxers).
Otherwise all timestamps are in units of the stream selected by stream_index
or if stream_index is -1, in AV_TIME_BASE units.
If flags contain AVSEEK_FLAG_ANY, then non-keyframes are treated as
keyframes (this may not be supported by all demuxers).
If flags contain AVSEEK_FLAG_BACKWARD, it is ignored.

@param s media file handle
@param stream_index index of the stream which is used as time base reference
@param min_ts smallest acceptable timestamp
@param ts target timestamp
@param max_ts largest acceptable timestamp
@param flags flags
@return >=0 on success, error code otherwise

@note This is part of the new seek API which is still under construction."
                        }
   

   :av_seek_frame {:rettype :int32
                   :argtypes [['s-ptr :pointer]
                              ['stream-index :int32]
                              ['timestamp :int64]
                              ['flags :int32]]
                   :check-errors? true
                   :doc "Seek to the keyframe at timestamp.
'timestamp' in 'stream_index'.

@param s media file handle
@param stream_index If stream_index is (-1), a default
stream is selected, and timestamp is automatically converted
from AV_TIME_BASE units to the stream specific time_base.
@param timestamp Timestamp in AVStream.time_base units
       or, if no stream is specified, in AV_TIME_BASE units.
@param flags flags which select direction and seeking mode
@return >= 0 on success"}

   })

(def ^{:tag 'long} AVIO_FLAG_READ  1)
(def ^{:tag 'long} AVIO_FLAG_WRITE 2)
(def ^{:tag 'long} AVIO_FLAG_READ_WRITE 3)

(def ^{:tag 'long} AVSEEK_FLAG_BACKWARD "seek backward" 1)
(def ^{:tag 'long} AVSEEK_FLAG_BYTE "seeking based on position in bytes" 2)
(def ^{:tag 'long} AVSEEK_FLAG_ANY "seek to any frame, even non-keyframes" 4)
(def ^{:tag 'long} AVSEEK_FLAG_FRAME "seeking based on frame number" 8)



(dt-ffi/define-library-interface avformat-def
  :check-error check-error
  :libraries ["avformat"])


(defn alloc-output-context
  [output-format]
  (resource/stack-resource-context
   (let [data-ptr (dt-ffi/make-ptr :pointer 0)
         format-ptr (dt-ffi/string->c output-format)]
     (avformat_alloc_output_context2 data-ptr nil format-ptr nil)
     (dt-ffi/ptr->struct (:datatype-name @av-context/av-format-context-def*)
                         (Pointer. (data-ptr 0))))))


(defn new-stream
  ^Map [avfmt-ctx codec]
  (->> (avformat_new_stream avfmt-ctx codec)
       (dt-ffi/ptr->struct (:datatype-name @av-context/av-stream-def*))))
