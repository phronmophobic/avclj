(ns avclj.avutil
  (:require [tech.v3.datatype.ffi :as dt-ffi]
            [avclj.avcodec :refer [check-error]]
            [tech.v3.datatype.errors :as errors]
            [tech.v3.resource :as resource])
  (:import [tech.v3.datatype.ffi Pointer]))


(def avutil-def
  {:av_dict_set {:rettype :int32
                 :argtypes [['pm :pointer]
                            ['key :string]
                            ['value :pointer]
                            ['flags :int32]]
                 :doc "Set a key in the dictionary.  If value is nil then key is removed"}
   :av_dict_count {:rettype :int32
                   :argtypes [['m :pointer]]
                   :doc "Get the count of the dictionary values"}
   :av_dict_free {:rettype :void
                  :argtypes [['pm :pointer]]
                  :doc "Free the dict and associated keys"}

   ;; int av_opt_set_int     (void *obj, const char *name, int64_t     val, int search_flags);
   :av_opt_set_int {:rettype :int32
                    :argtypes [['obj :pointer]
                               ['name :string]
                               ['val :int64]
                               ['search-flags :int32]]}

   :av_opt_set_bin {:rettype :int32
                    :argtypes [['obj :pointer]
                               ['name :string]
                               ['val :pointer]
                               ['size :int32]
                               ['search-flags :int32]]}

   :av_rescale {:rettype :int64
                :argtypes [['a :int64]
                           ['b :int64]
                           ['c :int64]]
                :doc "/**
 * Rescale a 64-bit integer with rounding to nearest.
 *
 * The operation is mathematically equivalent to `a * b / c`, but writing that
 * directly can overflow.
 *
 * This function is equivalent to av_rescale_rnd() with #AV_ROUND_NEAR_INF.
 *
 * @see av_rescale_rnd(), av_rescale_q(), av_rescale_q_rnd()
 */"}

   })

(dt-ffi/define-library-interface avutil-def
  :libraries ["avutil"]
  :check-error check-error)


(defn alloc-dict
  []
  (dt-ffi/make-ptr :pointer 0))


(defn free-dict!
  [dict]
  (av_dict_free dict))


(defn set-key-value!
  ([dict key val flags]
   (if val
     (resource/stack-resource-context
      (let [val-ptr (dt-ffi/string->c val {:resource-type :auto})]
        (av_dict_set dict key val-ptr flags)))
     (av_dict_set dict key nil flags)))
  ([dict key val]
   (set-key-value! dict key val 0)))


(defn dict-count
  [dict]
  (av_dict_count (Pointer. (dict 0))))



;;enum AVMediaType

(def ^{:tag 'long} AVMEDIA_TYPE_UNKNOWN -1);;< Usually treated as AVMEDIA_TYPE_DATA
(def ^{:tag 'long} AVMEDIA_TYPE_VIDEO 0)
(def ^{:tag 'long} AVMEDIA_TYPE_AUDIO 1)
(def ^{:tag 'long} AVMEDIA_TYPE_DATA 2);;< Opaque data information usually continuous
(def ^{:tag 'long}  AVMEDIA_TYPE_SUBTITLE 3)
(def ^{:tag 'long} AVMEDIA_TYPE_ATTACHMENT 4);;< Opaque data information usually sparse
(def ^{:tag 'long} AVMEDIA_TYPE_NB 5)


(defn media-type->int
  [media-type]
  (if-let [retval (get {:video AVMEDIA_TYPE_VIDEO
                        :audio AVMEDIA_TYPE_AUDIO}
                       media-type)]
    retval
    (errors/throwf "Failed to find media type %s" media-type)))



(def ^{:tag 'long} AV_DICT_MATCH_CASE "Only get an entry with exact-case key match. Only relevant in av_dict_get()." 1)
(def ^{:tag 'long} AV_DICT_IGNORE_SUFFIX "Return first entry in a dictionary whose first part corresponds to the search key,
  ignoring the suffix of the found key string. Only relevant in av_dict_get()." 2)
(def ^{:tag 'long} AV_DICT_DONT_STRDUP_KEY "Take ownership of a key that's been
  allocated with av_malloc() or another memory allocation function." 4)
(def ^{:tag 'long} AV_DICT_DONT_STRDUP_VAL "Take ownership of a value that's been
  allocated with av_malloc() or another memory allocation function." 8)
(def ^{:tag 'long} AV_DICT_DONT_OVERWRITE "Don't overwrite existing entries." 16)
(def ^{:tag 'long} AV_DICT_APPEND "If the entry already exists, append to it.  Note that no
delimiter is added, the strings are simply concatenated." 32)
(def ^{:tag 'long} AV_DICT_MULTIKEY "Allow to store several equal keys in the dictionary" 64)

(def ^{:tag 'long} AV_OPT_SEARCH_CHILDREN "Search in possible children of the given object first." 1)

(def ^{:tag 'long} AV_TIME_BASE "Internal time base represented as integer" 1000000) 
