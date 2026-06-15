mod d_util;
mod m_util;

// 将模块 utils 中的 mod 导出
pub use d_util::now;

// pub use m_util::*;
#[allow(unused_imports)]
pub use m_util::{abc, divide, math_util};
