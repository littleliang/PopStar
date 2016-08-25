# PopStar

## a solution to popstar

自动求解Popstar最优解，非全局最优

首先通过maven对项目进行打包

	maven clean install
	
在target目录得到PopStar-0.0.1.jar，当前版本为0.0.1，在jar包所在目录执行下列命令，文件为屏幕截图

	java -cp PopStar-0.0.1.jar popstar.solve.Solution "filepath" "length" "width"
	
采用opencv中matchTemplate函数的归一化相关系数匹配法对截图进行匹配，共匹配length*width次，得到输入数组，数组值为色块类型。如果不能正常运行，将jar包中nu\pattern\opencv\中相应dll库文件加入java执行路径中，一般为java安装文件夹下bin目录

在搜索过程中，对于前levelMaxDepth层，采用深度优先搜索，每一层保留与层数相关的节点数，在levelMaxDepth层之后，采取贪心算法，每一步选择下一层评分最优的节点进行点击，如果存在较大的连通域，则点击该连通域

运行100次随机生成的布局，平均分约4500，时间90s