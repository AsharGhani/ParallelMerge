import java.util.List;

import gitHelper.BranchInfo;

enum MergeMode {
	HostAndMergeBranches, AllBranches, AllForks
}

class ClientSettings 
{
	String 				user;
	String 				repo;
	List<BranchInfo> 	branches;
	String 				controllerIP;
	int				 	controllerPort;
	MergeMode mergeMode;
}
	
	/*static class LocalRepository {
	String userName;
	String repositoryName;
	String repositoryURL;
	String localRepositoryDir;
	String localBaseDir;
	String[] populatedBranches;

	LocalRepository(String localBaseDir) {
		this.localBaseDir = localBaseDir;
		initialize();
	}

	boolean initialize() {
		// detect existing repository in the base dir

		// detect and add existing populated branches

		return false;
	}

	boolean AddPopulatedBranch(String branchName) {
		return false;
	}

	boolean Refresh() {
		// Reset

		// Re-initialize();
		initialize();

		// rescan existing branches;
		return false;
	}
}
*/