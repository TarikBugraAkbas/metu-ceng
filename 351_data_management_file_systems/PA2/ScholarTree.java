import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class ScholarTree {

	public ScholarNode primaryRoot;        //root of the primary B+ tree
	public ScholarNode secondaryRoot;    //root of the secondary B+ tree

	public ScholarTree(Integer order) {
		ScholarNode.order = order;
		primaryRoot = new ScholarNodePrimaryLeaf(null);
		primaryRoot.level = 0;
		secondaryRoot = new ScholarNodeSecondaryLeaf(null);
		secondaryRoot.level = 0;
	}

	public void addPaper(CengPaper Paper) {
		// TODO: Implement this method

		int i;
		//ADDING PRIMARY
		ScholarNodePrimaryLeaf currLeaf = whichLeaf(Paper);
		ArrayList<CengPaper> allPapers = currLeaf.getPapers();
		for (i = 0; i < allPapers.size(); i++) {
			if (currLeaf.paperAtIndex(i).paperId() > Paper.paperId()) {
				break;
			}
		}
		currLeaf.addPaper(i, Paper); //put the paper



		//splitting begins
		if (currLeaf.paperCount() == 2 * ScholarNode.order + 1) {
			if (currLeaf.getParent() == null) //create new root
			{
				ScholarNodePrimaryIndex newRoot = new ScholarNodePrimaryIndex(null);
				ScholarNodePrimaryLeaf newLeaf = new ScholarNodePrimaryLeaf(newRoot);

				i = 0;
				for (int j = ScholarNode.order; j < allPapers.size(); j++) {
					newLeaf.addPaper(i, allPapers.get(j));
					i++;
				}
				allPapers.subList(ScholarNode.order, allPapers.size()).clear();
				newRoot.addPaperToIndex(0, newLeaf.paperIdAtIndex(0));
				newRoot.setParent(null);
				newRoot.addChildToIndex(0, currLeaf);
				newRoot.addChildToIndex(1, newLeaf);
				currLeaf.setParent(newRoot);
				primaryRoot = newRoot;
			} else //root already exists
			{
				ScholarNode parent = currLeaf.getParent();
				ScholarNodePrimaryIndex parentIndex = (ScholarNodePrimaryIndex) parent;
				ScholarNodePrimaryLeaf newLeaf = new ScholarNodePrimaryLeaf(parent);

				i = 0;
				for (int j = ScholarNode.order; j < allPapers.size(); j++) {
					newLeaf.addPaper(i, allPapers.get(j));
					i++;
				}

				allPapers.subList(ScholarNode.order, allPapers.size()).clear();


				int pushUp = newLeaf.paperIdAtIndex(0);

				for (i = 0; i < parentIndex.paperIdCount(); i++) {
					if (parentIndex.paperIdAtIndex(i) > pushUp) {
						break;
					}
				}
				parentIndex.addPaperToIndex(i, pushUp);

				parentIndex.addChildToIndex(i + 1, newLeaf);
				//If parent is full, then what?
				if (parentIndex.paperIdCount() == 2 * ScholarNode.order + 1) {
					splitParent(parentIndex);
				}


			}


		}

		//ADDING SECONDARY
		ScholarNodeSecondaryLeaf secLeaf = whichLeaf2(Paper);
		boolean equals = false;
		ArrayList<ArrayList<Integer>> buckets = secLeaf.getPaperIdBucket();
		for (i = 0; i < secLeaf.getJournals().size(); i++) {
			//help what to do
			if(secLeaf.journalAtIndex(i).equals(Paper.journal()))
			{
				equals = true;
				break;

			}
			if (secLeaf.journalAtIndex(i).compareTo(Paper.journal()) > 0) {
				break;
			}
		}
		if(equals){
			if(i < buckets.size() && buckets.get(i) != null)
			{
				buckets.get(i).add(Paper.paperId());
			}
			else{
				throw new IllegalStateException("Bucket not properly iniatlized at index " + i);

			}

		}
		else{
			buckets.add(i, new ArrayList<>());
			buckets.get(i).add(Paper.paperId());
			secLeaf.getJournals().add(i, Paper.journal());
		}
		if(secLeaf.journalCount() == 2 * ScholarNode.order + 1){
			if(secLeaf.getParent() == null){
				ScholarNodeSecondaryIndex newRoot = new ScholarNodeSecondaryIndex(null);
				ScholarNodeSecondaryLeaf newLeaf = new ScholarNodeSecondaryLeaf(newRoot);

				i = 0;
				for(int j = ScholarNode.order; j < secLeaf.journalCount(); j++){
					newLeaf.getJournals().add(i, secLeaf.journalAtIndex(j));
					newLeaf.getPaperIdBucket().add(i, buckets.get(j));
					i++;
				}
				buckets.subList(ScholarNode.order, buckets.size()).clear();
				secLeaf.getJournals().subList(ScholarNode.order, secLeaf.journalCount()).clear();


				String copyUp = newLeaf.journalAtIndex(0);

				newRoot.addJournalToIndex(0, copyUp);
				newRoot.addChildToIndex(0,secLeaf);
				newRoot.addChildToIndex(1,newLeaf);
				secLeaf.setParent(newRoot);
				secondaryRoot = newRoot;


			}
			else
			{
				ScholarNode parent = secLeaf.getParent();
				ScholarNodeSecondaryIndex parentIndex = (ScholarNodeSecondaryIndex) parent;
				ScholarNodeSecondaryLeaf newLeaf = new ScholarNodeSecondaryLeaf(parent);

				i = 0;
				for(int j = ScholarNode.order; j < secLeaf.journalCount(); j++) {
					newLeaf.getJournals().add(i, secLeaf.journalAtIndex(j));
					newLeaf.getPaperIdBucket().add(i, buckets.get(j));
					i++;
				}
				buckets.subList(ScholarNode.order, buckets.size()).clear();
				secLeaf.getJournals().subList(ScholarNode.order, secLeaf.journalCount()).clear();

				String copyUp = newLeaf.journalAtIndex(0);
				for(i = 0; i < parentIndex.journalCount(); i++)
				{
					if(parentIndex.journalAtIndex(i).compareTo(copyUp) > 0)
					{
						break;
					}
				}
				parentIndex.addJournalToIndex(i, copyUp);

				parentIndex.addChildToIndex(i + 1, newLeaf);
				//If parent is full, then:
				if(parentIndex.journalCount() == 2 * ScholarNode.order + 1){
					splitParent2(parentIndex);
				}


			}

		}
	}

	public CengPaper searchPaper(Integer paperId) {
		ScholarNode currNode = primaryRoot;
		int i, foundIdx = -1;
		boolean found = false;
		String ind = "";

		while (currNode.getType() == ScholarNodeType.Internal) {
			ScholarNodePrimaryIndex currentIndexNode = (ScholarNodePrimaryIndex) currNode;
			System.out.println(ind + "<index>");
			for(int k = 0; k < currentIndexNode.paperIdCount(); k++)
			{
				System.out.println(ind + currentIndexNode.paperIdAtIndex(k));
			}
			for (i = 0; i < currentIndexNode.paperIdCount(); i++) {

				if (currentIndexNode.paperIdAtIndex(i) > paperId) {
					break;
				}
			}
			System.out.println(ind + "</index>");
				currNode = currentIndexNode.getChildrenAt(i);

			ind = ind.concat( "\t");

		}
		if (currNode.getType() == ScholarNodeType.Leaf) {
			ScholarNodePrimaryLeaf currLeaf = (ScholarNodePrimaryLeaf) currNode;

			for (i = 0; i < currLeaf.paperCount(); i++) {
				CengPaper currPaper = currLeaf.getPapers().get(i);


				if (currPaper.paperId().equals(paperId)) {
					System.out.println(ind + "<data>");
					System.out.println(ind + "<record>" + paperId + "|" + currPaper.journal() + "|" + currPaper.paperName() + "|" + currPaper.author() + "</record>");
					System.out.println(ind + "</data>");
					return currPaper;

				}
			}
			System.out.println("Could not find " + paperId);
			return null;
		}
	return null;
	}


	public void searchJournal(String journal) {
		ScholarNode currNode = secondaryRoot;
		int i;
		String ind = "";

		while (currNode.getType() == ScholarNodeType.Internal) {
			ScholarNodeSecondaryIndex currentIndexNode = (ScholarNodeSecondaryIndex) currNode;
			System.out.println(ind + "<index>");
			for(int k = 0; k < currentIndexNode.journalCount(); k++)
			{
				System.out.println(ind + currentIndexNode.journalAtIndex(k));
			}
			for (i = 0; i < currentIndexNode.journalCount(); i++) {

				if (currentIndexNode.journalAtIndex(i).compareTo(journal) > 0) {
					break;
				}
			}
			System.out.println(ind + "</index>");
			currNode = currentIndexNode.getChildrenAt(i);

			ind = ind.concat( "\t");

		}
		if (currNode.getType() == ScholarNodeType.Leaf) {
			ScholarNodeSecondaryLeaf currLeaf = (ScholarNodeSecondaryLeaf) currNode;

			for (i = 0; i < currLeaf.journalCount(); i++) {
				String currJournal = currLeaf.journalAtIndex(i);

				if (currJournal.equals(journal)) {
					System.out.println(ind + "<data>");
					System.out.println(ind + currJournal);


					for(int k = 0; k < currLeaf.papersAtIndex(i).size(); k++)
					{
						int paperId = currLeaf.papersAtIndex(i).get(k);
						CengPaper paper = findPaper(paperId);
						if(paper != null){
							System.out.println(ind + "\t" + "<record>" + paper.paperId() + "|" +
									paper.journal() + "|" + paper.paperName() +
									"|" + paper.author() + "</record>");
						}
					}


					System.out.println(ind + "</data>");
					return;

				}
			}
			System.out.println("Could not find " + journal);
		}
	}

	public void printPrimaryScholar() {
		Stack<ScholarNode> nodeStack = new Stack<ScholarNode>();
		nodeStack.push(primaryRoot);
		Stack<String> indStack = new Stack<String>();
		indStack.push("");
		while(!nodeStack.empty())
		{
			ScholarNode currNode = nodeStack.pop();
			String ind = indStack.pop();
			if(currNode.getType() == ScholarNodeType.Internal)
			{
				ScholarNodePrimaryIndex currentIndexNode = (ScholarNodePrimaryIndex) currNode;
				System.out.println(ind + "<index>");
				for(int i = 0; i < currentIndexNode.paperIdCount(); i++)
				{
					System.out.println(ind + currentIndexNode.paperIdAtIndex(i));
				}
				System.out.println(ind + "</index>");

				for(int i = currentIndexNode.getAllChildren().size()-1; i >= 0; i--)
				{
					nodeStack.push(currentIndexNode.getAllChildren().get(i));
					indStack.push(ind + "\t");
				}
			}
			else if(currNode.getType() == ScholarNodeType.Leaf)
			{
				ScholarNodePrimaryLeaf currLeaf = (ScholarNodePrimaryLeaf) currNode;
				System.out.println(ind + "<data>");

				for(int i = 0; i < currLeaf.paperCount(); i++)
				{
					CengPaper currPaper = currLeaf.getPapers().get(i);

					System.out.println(ind + "<record>" + currPaper.paperId() + "|" + currPaper.journal() + "|" +
							currPaper.paperName() + "|" +
							currPaper.author() + "</record>");
				}
				System.out.println(ind + "</data>");

			}
		}
	}

	public void printSecondaryScholar() {
		Stack<ScholarNode> nodeStack = new Stack<ScholarNode>();
		nodeStack.push(secondaryRoot);
		Stack<String> indStack = new Stack<String>();
		indStack.push("");
		while(!nodeStack.empty())
		{
			ScholarNode currNode = nodeStack.pop();
			String ind = indStack.pop();
			if(currNode.getType() == ScholarNodeType.Internal)
			{
				ScholarNodeSecondaryIndex currentIndexNode = (ScholarNodeSecondaryIndex) currNode;
				System.out.println(ind + "<index>");
				for(int i = 0; i < currentIndexNode.journalCount(); i++)
				{
					System.out.println(ind + currentIndexNode.journalAtIndex(i));
				}
				System.out.println(ind + "</index>");

				for(int i = currentIndexNode.getAllChildren().size()-1; i >= 0; i--)
				{
					nodeStack.push(currentIndexNode.getAllChildren().get(i));
					indStack.push(ind + "\t");
				}
			}
			else if(currNode.getType() == ScholarNodeType.Leaf)
			{
				ScholarNodeSecondaryLeaf currLeaf = (ScholarNodeSecondaryLeaf) currNode;
				System.out.println(ind + "<data>");

				for(int i = 0; i < currLeaf.journalCount(); i++)
				{
					System.out.println(ind + currLeaf.journalAtIndex(i));
					for(int k = 0; k < currLeaf.papersAtIndex(i).size(); k++)
					{
						int paperId = currLeaf.papersAtIndex(i).get(k);

						System.out.println(ind + "\t" + "<record>" + paperId + "</record>");
					}


				}
				System.out.println(ind + "</data>");

			}

		}
	}

	// Extra functions if needed
	public ScholarNodePrimaryLeaf whichLeaf(CengPaper paper) {
		ScholarNode currNode = primaryRoot;
		Integer paperId = paper.paperId();
		int i;

		while (currNode.getType() == ScholarNodeType.Internal) {
			ScholarNodePrimaryIndex currentIndexNode = (ScholarNodePrimaryIndex) currNode;

			// Find the correct child node for the given paperId
			for (i = 0; i < currentIndexNode.paperIdCount(); i++) {
				if (currentIndexNode.paperIdAtIndex(i) > paperId) {
					break;
				}
			}
			currNode = ((ScholarNodePrimaryIndex) currNode).getChildrenAt(i);
		}
		return (ScholarNodePrimaryLeaf) currNode;
	}

	public ScholarNodeSecondaryLeaf whichLeaf2(CengPaper paper) {
	ScholarNode currNode = secondaryRoot;
	Integer paperId = paper.paperId();
	int i;

	while (currNode.getType() == ScholarNodeType.Internal) {
		ScholarNodeSecondaryIndex currentIndexNode = (ScholarNodeSecondaryIndex) currNode;

		// Find the correct child node for the given paperId
		for (i = 0; i < currentIndexNode.journalCount(); i++) {
			if (currentIndexNode.journalAtIndex(i).compareTo(paper.journal()) > 0 ) {
				break;
			}
		}
		currNode = ((ScholarNodeSecondaryIndex) currNode).getChildrenAt(i);
	}
	return (ScholarNodeSecondaryLeaf) currNode;
	}

	public void splitParent(ScholarNodePrimaryIndex parent) {
		int i;
		if (parent.getParent() == null) //new root creation
		{
			ScholarNodePrimaryIndex newRoot = new ScholarNodePrimaryIndex(null);
			ScholarNodePrimaryIndex newNode = new ScholarNodePrimaryIndex(newRoot);
			ArrayList<ScholarNode> children = parent.getAllChildren();
			ArrayList<Integer> papers = parent.getAllPapers();

			i = 0;


			int pushUp = parent.paperIdAtIndex(ScholarNode.order);
			for (int j = ScholarNode.order + 1; j < papers.size(); j++) {
				newNode.addPaperToIndex(i, parent.paperIdAtIndex(j));
				i++;
			}
			i = 0;
			for(int j = ScholarNode.order + 1; j < children.size(); j++) {
				newNode.addChildToIndex(i, parent.getChildrenAt(j));
				parent.getChildrenAt(j).setParent(newNode);
				i++;
			}


			children.subList(ScholarNode.order + 1, children.size()).clear();
			papers.subList(ScholarNode.order, papers.size()).clear();

			newRoot.addChildToIndex(0, parent);
			newRoot.addChildToIndex(1, newNode);
			newRoot.addPaperToIndex(0, pushUp);
			parent.setParent(newRoot);
			newNode.setParent(newRoot);
			primaryRoot = newRoot;
			return;
		} else {
			ScholarNode grandparent = parent.getParent();
			ScholarNodePrimaryIndex grandParentIndex = (ScholarNodePrimaryIndex) grandparent;
			ScholarNodePrimaryIndex newNode = new ScholarNodePrimaryIndex(grandparent);
			ArrayList<ScholarNode> children = parent.getAllChildren();
			ArrayList<Integer> papers = parent.getAllPapers();

			i = 0;

			for (int j = ScholarNode.order + 1; j < papers.size(); j++) {
				newNode.addPaperToIndex(i, parent.paperIdAtIndex(j));
				i++;
			}
			i = 0;
			for(int j = ScholarNode.order + 1; j < children.size(); j++) {
				newNode.addChildToIndex(i, parent.getChildrenAt(j));
				parent.getChildrenAt(j).setParent(newNode);
				i++;
			}
			int pushUp = parent.paperIdAtIndex(ScholarNode.order);
			children.subList(ScholarNode.order+1, children.size()).clear();
			papers.subList(ScholarNode.order, papers.size()).clear();

			for (i = 0; i < grandParentIndex.getAllPapers().size(); i++) {
				if (grandParentIndex.getAllPapers().get(i) > pushUp) {
					break;
				}
			}
			grandParentIndex.addPaperToIndex(i, pushUp);
			grandParentIndex.addChildToIndex(i + 1, newNode);
			if (grandParentIndex.paperIdCount() == 2 * ScholarNode.order + 1) {
				splitParent(grandParentIndex);
			}
		}
	}

	public void splitParent2(ScholarNodeSecondaryIndex parent) {
		int i;
		if (parent.getParent() == null) //new root creation
		{
			ScholarNodeSecondaryIndex newRoot = new ScholarNodeSecondaryIndex(null);
			ScholarNodeSecondaryIndex newNode = new ScholarNodeSecondaryIndex(newRoot);
			ArrayList<ScholarNode> children = parent.getAllChildren();
			ArrayList<String> journals = parent.getAllJournals();
			i = 0;


			String pushUp = parent.journalAtIndex(ScholarNode.order);
			for (int j = ScholarNode.order + 1; j < journals.size(); j++) {
				newNode.addJournalToIndex(i, parent.journalAtIndex(j));
				i++;
			}

			i = 0;
			for(int j = ScholarNode.order + 1; j < children.size(); j++) {
				newNode.addChildToIndex(i, parent.getChildrenAt(j));
				parent.getChildrenAt(j).setParent(newNode);
				i++;
			}


			children.subList(ScholarNode.order + 1, children.size()).clear();
			journals.subList(ScholarNode.order, journals.size()).clear();

			newRoot.addChildToIndex(0, parent);
			newRoot.addChildToIndex(1, newNode);
			newRoot.addJournalToIndex(0, pushUp);
			parent.setParent(newRoot);
			newNode.setParent(newRoot);
			primaryRoot = newRoot;
		} else {

			ScholarNode grandparent = parent.getParent();
			ScholarNodeSecondaryIndex grandParentIndex = (ScholarNodeSecondaryIndex) grandparent;
			ScholarNodeSecondaryIndex newNode = new ScholarNodeSecondaryIndex(grandparent);
			ArrayList<ScholarNode> children = parent.getAllChildren();
			ArrayList<String> journals = parent.getAllJournals();

			i = 0;

			for (int j = ScholarNode.order + 1; j < journals.size(); j++) {
				newNode.addJournalToIndex(i, parent.journalAtIndex(j));
				i++;
			}
			i = 0;
			for(int j = ScholarNode.order + 1; j < children.size(); j++) {
				newNode.addChildToIndex(i, parent.getChildrenAt(j));
				parent.getChildrenAt(j).setParent(newNode);
				i++;
			}
			String pushUp = parent.journalAtIndex(ScholarNode.order);
			children.subList(ScholarNode.order+1, children.size()).clear();
			journals.subList(ScholarNode.order, journals.size()).clear();

			for (i = 0; i < grandParentIndex.getAllJournals().size(); i++) {
				if (grandParentIndex.getAllJournals().get(i).compareTo(pushUp) > 0) {
					break;
				}
			}
			grandParentIndex.addJournalToIndex(i, pushUp);
			grandParentIndex.addChildToIndex(i + 1, newNode);
			if (grandParentIndex.journalCount() == 2 * ScholarNode.order + 1) {
				splitParent2(grandParentIndex);
			}
		}

	}

	public CengPaper findPaper(Integer paperId){
		int i;
		ScholarNode currNode = primaryRoot;
		while (currNode.getType() == ScholarNodeType.Internal){
			ScholarNodePrimaryIndex currentIndexNode = (ScholarNodePrimaryIndex) currNode;
			for(i = 0; i < currentIndexNode.paperIdCount(); i++)
			{
				if(currentIndexNode.paperIdAtIndex(i) > paperId){
					break;
				}
			}
			currNode = currentIndexNode.getChildrenAt(i);
			if (currNode.getType() == ScholarNodeType.Leaf) {
				ScholarNodePrimaryLeaf currLeaf = (ScholarNodePrimaryLeaf) currNode;

				for (i = 0; i < currLeaf.paperCount(); i++) {
					CengPaper currPaper = currLeaf.getPapers().get(i);


					if (currPaper.paperId().equals(paperId)) {
						return currPaper;

					}
				}
				return null;
			}

		}
		return null;
	}
}





