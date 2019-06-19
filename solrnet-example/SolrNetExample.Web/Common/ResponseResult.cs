using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SolrNetExample.Web.Common
{
    /// <summary>
    /// 通用 Api 响应结果类。
    /// </summary>
    [Serializable]
    public class ResponseResult
    {
        #region Fields...

        #endregion

        #region Constructors...

        /// <summary>
        /// 初始化 <see cref="ResponseResult"/> 类的新实例。
        /// </summary>
        public ResponseResult()
        {
            Status = 0;
            ResponseData = new object();
            Error = String.Empty;
        }

        #endregion

        #region Properties...

        /// <summary>
        /// 获取或设置响应错误信息。
        /// </summary>

        /// <summary>
        /// 获取或设置响应状态值。
        /// </summary>
        public int Status { get; set; }

        /// <summary>
        /// 获取或设置响应成功的数据信息。
        /// </summary>
        public object ResponseData { get; set; }

        /// <summary>
        /// 获取或设置响应错误信息。
        /// </summary>
        public string Error { get; set; }


        #endregion
    }

    /// <summary>
    /// 支持泛型的通用 Api 响应结果类。
    /// </summary>
    /// <typeparam name="T">指定的对象类型。</typeparam>
    public class ResponseResult<T> : ResponseResult
    {
        /// <summary>
        /// 获取或设置响应成功的数据信息。
        /// </summary>
        public new T ResponseData { get; set; }

        /// <summary>
        /// 初始化 <see cref="ResponseResult{T}"/> 类的新实例。
        /// </summary>
        public ResponseResult(T responseData) : base()
        {
            ResponseData = responseData;
        }
        /// <summary>
        /// 使用指定的参数初始化 <see cref="ResponseResult{T}"/> 类的新实例。
        /// </summary>
        public ResponseResult(int status, string error, T responseData)
        {
            Status = status;
            Error = error;
            ResponseData = responseData;
        }
    }
}
