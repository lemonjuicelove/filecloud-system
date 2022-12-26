package src.test;


import net.sf.jsqlparser.schema.Server;
import src.action.DownFile;
import src.bean.SiteInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class DownTest {
	
	public static void main(String args[]) throws Exception {
		SiteInfo siteInfo = new SiteInfo("https://xafj-cu11.baidupcs.com/file/e55cf37b55dcb7f51e0f0d0d38ae0020?bkt=en-2a4ba40c42c88fab34425c99db52b46e8b830c8e6eea526f44891ccdc7e71872a9df02a867cbb0d15c826719001bbb0f58f279747030e6340e5ccfe5520c4b0e&fid=86214459-250528-409785656055456&time=1651737880&sign=FDTAXUbGERLQlBHSKfWqiu-DCb740ccc5511e5e8fedcff06b081203-8CkMlRJszS0NNv9eL1vRo%2BO4UCw%3D&to=419&size=41270214&sta_dx=41270214&sta_cs=57763&sta_ft=pdf&sta_ct=7&sta_mt=7&fm2=MH%2CXian%2CAnywhere%2C%2Chubei%2Ccnc&ctime=1517469594&mtime=1517471656&resv0=-1&resv1=0&resv2=rlim&resv3=5&resv4=41270214&vuk=2713943257&iv=0&htype=&randtype=&tkbind_id=0&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=en-ee75e9148894a7b45c59f6ab7f679daa5720b84c53419e7f2f19157b8d5fb36d9daac138422f7dfcb5bdb7e5b13bf86e3e5e4094769ef5aa305a5e1275657320&sl=76480590&expires=8h&rt=sh&r=837272150&vbdid=2577133198&fin=598536+MySQL%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95InnoDB%E5%AD%98%E5%82%A8%E5%BC%95%E6%93%8E%E7%AC%AC2%E7%89%88.pdf&fn=598536+MySQL%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95InnoDB%E5%AD%98%E5%82%A8%E5%BC%95%E6%93%8E%E7%AC%AC2%E7%89%88.pdf&rtype=1&dp-logid=8656054616875642865&dp-callid=0.1&hps=1&tsl=80&csl=80&fsl=-1&csign=Wu%2Fw2vDtU1S5IDPmHvPMIzVv6XU%3D&so=0&ut=6&uter=4&serv=0&uc=402656090&ti=5e666840c78f1973ccdb8210f0b77a0dadd09fc211649181305a5e1275657320&hflag=30&from_type=0&adg=c_dcb579445bc984585c45f3cab05d8082&reqlabel=250528_f_261d042eef4664ffd2781fa433d4c096_-1_ed440a3ed9239279a3d7d5982a4a5203&by=themis&resvsflag=1-0-0-1-1-1", "C:\\Users\\73561\\Desktop\\项目", "MYsql", 3);
		
		DownFile downFile = new DownFile(siteInfo);
		
		downFile.startDown();
	}

}
