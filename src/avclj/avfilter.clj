(ns avclj.avfilter
  (:require [tech.v3.datatype.ffi :as dt-ffi]
            [avclj.avcodec :refer [check-error]]
            [tech.v3.datatype.errors :as errors]
            [tech.v3.resource :as resource])
  (:import [tech.v3.datatype.ffi Pointer]))


(def avfilter-def
  {

   ;; AVFilterGraph *avfilter_graph_alloc(void);
   :avfilter_graph_alloc {:rettype :pointer
                          :argtypes []
                          }

   ;; const AVFilter *avfilter_get_by_name(const char *name);
   :avfilter_get_by_name {:rettype :pointer
                          :argtypes [['name :string]]}

   ;; AVFilterContext *avfilter_graph_alloc_filter(AVFilterGraph *graph,
   ;;                                           const AVFilter *filter,
   ;;                                           const char *name);
   :avfilter_graph_alloc_filter {:rettype :pointer
                                 :argtypes [['graph :pointer]
                                            ['filter :pointer]
                                            ['name :string]]}

   ;; int avfilter_graph_create_filter(AVFilterContext **filt_ctx, const AVFilter *filt,
   ;;                               const char *name, const char *args, void *opaque,
   ;;                               AVFilterGraph *graph_ctx);
   :avfilter_graph_create_filter {:rettype :int32
                                  :argtypes [['filter-ctx :pointer]
                                             ['filter :pointer]
                                             ['name :string]
                                             ['args :pointer?]
                                             ['opaque :pointer?]
                                             ['graph-ctx :pointer]]
                                  :check-error? true}

   

   
   ;; int avfilter_init_str(AVFilterContext *ctx, const char *args);
   :avfilter_init_str {:rettype :int32
                       :argtypes [['ctx :pointer]
                                  ['args :pointer?]]
                       :check-error? true}

   ;; int avfilter_link(AVFilterContext *src, unsigned srcpad,
   ;;                AVFilterContext *dst, unsigned dstpad);
   :avfilter_link {:rettype :int32
                   :argtypes [['src :pointer]
                              ['srcpad :int32]
                              ['dst :pointer]
                              ['dstpad :int32]]
                   :check-error? true}


   ;; int avfilter_graph_config(AVFilterGraph *graphctx, void *log_ctx);
   :avfilter_graph_config {:rettype :int32
                           :argtypes [['graphctx :pointer]
                                      ['log_ctx :pointer?]]
                           :check-error? true}

   ;; void avfilter_graph_free(AVFilterGraph **graph);
   :avfilter_graph_free {:rettype :void
                         :argtypes [['graph :pointer]]}


   ;; int av_buffersrc_write_frame(AVFilterContext *ctx, const AVFrame *frame);
   :av_buffersrc_write_frame {:rettype :int32
                              :argtypes [['ctx :pointer]
                                         ['frame :pointer?]]
                              :check-error? true
                              }

   ;; int av_buffersink_get_frame(AVFilterContext *ctx, AVFrame *frame);
   :av_buffersink_get_frame {:rettype :int32
                             :argtypes [['ctx :pointer]
                                        ['frame :pointer]]}
   ,
   })


(dt-ffi/define-library-interface avfilter-def
  :libraries ["avfilter"]
  :check-error check-error)

